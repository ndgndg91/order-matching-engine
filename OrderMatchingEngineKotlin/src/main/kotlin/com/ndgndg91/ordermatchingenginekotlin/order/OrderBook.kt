package com.ndgndg91.ordermatchingenginekotlin.order

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet
import kotlin.collections.ArrayList

class OrderBook(private val symbol: Symbol) {
    private val log: Logger = LoggerFactory.getLogger(OrderBook::class.java)

    private val limitBids: SortedSet<OrderEntry> = ConcurrentSkipListSet { e1, e2 ->
        val c = e2.price.compareTo(e1.price)
        if (c == 0) e1.timestamp.compareTo(e2.timestamp) else c
    }
    private val limitAsks: SortedSet<OrderEntry> = ConcurrentSkipListSet { e1, e2 ->
        val c = e1.price.compareTo(e2.price)
        if (c == 0) e1.timestamp.compareTo(e2.timestamp) else c
    }

    private val marketBids: SortedSet<OrderEntry> = ConcurrentSkipListSet { e1, e2 ->
        e1.timestamp.compareTo(e2.timestamp)
    }
    private val marketAsks: SortedSet<OrderEntry> = ConcurrentSkipListSet { e1, e2 ->
        e1.timestamp.compareTo(e2.timestamp)
    }

    private fun selectQueue(priceType: PriceType, orderType: OrderType): SortedSet<OrderEntry> {
        return when (priceType) {
            PriceType.MARKET -> marketQueue(orderType)
            PriceType.LIMIT -> limitQueue(orderType)
        }
    }

    private fun marketQueue(orderType: OrderType): SortedSet<OrderEntry> {
        return when (orderType) {
            OrderType.BID -> this.marketBids
            OrderType.ASK -> this.marketAsks
        }
    }

    private fun limitQueue(orderType: OrderType): SortedSet<OrderEntry> {
        return when (orderType) {
            OrderType.BID -> this.limitBids
            OrderType.ASK -> this.limitAsks
        }
    }

    fun addOrder(order: Order) {
        val queue = selectQueue(order.priceType, order.orderType)
        if (kotlin.runCatching { queue.first { it.orderId == order.orderId } }.isSuccess) {
            log.info("already exists, failed to add $order")
            return
        } else {
            val e = OrderEntry(order)
            queue.add(e)
            log.info("success to add $order")
        }
    }

    fun modifyOrder(order: Order) {
        val queue = selectQueue(order.priceType, order.orderType)
        kotlin.runCatching { queue.first { it.orderId == order.orderId } }
            .onSuccess { queue.remove(it) }.also { log.info("success to remove origin : ${it.getOrThrow()}") }
        val e = OrderEntry(order)
        queue.add(e).also { log.info("success to modify : $e") }
    }

    fun cancelOrder(order: Order) {
        val queue = selectQueue(order.priceType, order.orderType)

        kotlin.runCatching { queue.first { it.orderId == order.orderId } }
            .onSuccess { queue.remove(it) }
    }

    fun find(orderType: OrderType, orderId: String): OrderEntry? = when (orderType) {
        OrderType.ASK -> {
            val inLimitAsks = kotlin.runCatching { this.limitAsks.first { it.orderId == orderId } }
            if (inLimitAsks.isSuccess) {
                inLimitAsks.getOrThrow()
            }

            val inMarketAsks = kotlin.runCatching { this.marketAsks.first { it.orderId == orderId } }
            if (inMarketAsks.isSuccess) {
                inMarketAsks.getOrThrow()
            }

            null
        }
        OrderType.BID -> {
            val inLimitBids = kotlin.runCatching { this.limitBids.first { it.orderId == orderId } }
            if (inLimitBids.isSuccess) {
                inLimitBids.getOrThrow()
            }


            val inMarketBids = kotlin.runCatching { this.marketBids.first { it.orderId == orderId } }
            if (inMarketBids.isSuccess) {
                inMarketBids.getOrThrow()
            }

            null
        }
    }


    fun bidsPoll(): OrderEntry? = if (!this.marketBids.isEmpty()) {
        val first = this.marketBids.first()
        this.marketBids.remove(first)
        first
    } else {
        val first = this.limitBids.first()
        this.limitBids.remove(first)
        first
    }


    fun asksPoll(): OrderEntry? = if (!this.marketAsks.isEmpty()) {
        val first = this.marketAsks.first()
        this.marketAsks.remove(first)
        first
    } else {
        val first = this.limitAsks.first()
        this.limitAsks.remove(first)
        first
    }

    fun match(priceType: PriceType, orderType: OrderType): MatchResult? {
        return when (priceType) {
            PriceType.LIMIT -> matchLimitOrder()
            PriceType.MARKET -> matchMarketOrder(orderType)
        }
    }

    private fun matchLimitOrder(): MatchResult? {
        val bid = peek(this.limitBids)
        val ask = peek(this.limitAsks)
        if (ask == null || bid == null) {
            return null
        }

        val c = bid.price.compareTo(ask.price)
        var d = bid.shares() - ask.shares()
        // matched
        if (c >= 0) {
            if (d == 0) { // exact matched
                poll(this.limitBids)
                poll(this.limitAsks)
                return MatchResult.exact(bid, symbol, ask)
            } else if (d > 0) { // bid has more shares and need more ask : partial matched
                val tAsks = ArrayList<OrderEntry>()
                tAsks.add(poll(this.limitAsks)!!)
                while (d > 0) {
                    val peek = peek(this.limitAsks)
                    if (peek == null || bid.price < peek.price) {
                        this.limitAsks.addAll(tAsks)
                        return null
                    }

                    if (d < peek.shares()) {
                        peek.partialMatched(bid, d)
                        tAsks.add(peek)
                        poll(this.limitBids)
                        d = 0
                    } else if (d > peek.shares()) {
                        d -= peek.shares()
                        tAsks.add(poll(this.limitAsks)!!)
                    } else {
                        tAsks.add(poll(this.limitAsks)!!)
                        break
                    }
                }

                return MatchResult.bigBid(bid, symbol, tAsks)
            } else { // ask has more shares and need more bid : partial matched
                ask.partialMatched(bid)
                poll(this.limitBids)
                return MatchResult.bigAsk(bid, symbol, ask)
            }
        } else { // not matched
            return null
        }
    }

    private fun matchMarketOrder(orderType: OrderType): MatchResult? {
        return when (orderType) {
            OrderType.BID -> matchMarketBidOrder()
            OrderType.ASK -> matchMarketAskOrder()
        }
    }

    private fun matchMarketBidOrder(): MatchResult? {
        val mBid = peek(this.marketBids)
        val lAsk = peek(this.limitAsks)
        if (mBid == null || lAsk == null) {
            return null
        }

        var d = mBid.shares() - lAsk.shares()
        if (d == 0) { // exact matched
            poll(this.marketBids)
            poll(this.limitAsks)
            return MatchResult.exact(mBid, symbol, lAsk)
        } else if (d > 0) { // market bid has more shares and need more limit ask
            val tAsks = ArrayList<OrderEntry>()
            tAsks.add(poll(this.limitAsks)!!)
            while (d > 0) {
                val peek = peek(this.limitAsks)
                if (peek == null) {
                    this.limitAsks.addAll(tAsks)
                    return null
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mBid, d)
                    tAsks.add(peek)
                    poll(this.marketBids)
                    d = 0
                } else if (d > peek.shares()) {
                    d -= peek.shares()
                    tAsks.add(poll(this.limitAsks)!!)
                } else {
                    tAsks.add(poll(this.limitAsks)!!)
                    break
                }
            }

            return MatchResult.bigBid(mBid, symbol, tAsks)
        } else { // limit ask has more shares and need more market bid
            lAsk.partialMatched(mBid)
            poll(this.marketBids)
            return MatchResult.bigAsk(mBid, symbol, lAsk)
        }
    }

    private fun matchMarketAskOrder(): MatchResult? {
        val mAsk = peek(this.marketAsks)
        val lBid = peek(this.limitBids)
        if (mAsk == null || lBid == null) {
            return null
        }

        var d = mAsk.shares() - lBid.shares()
        if (d == 0) { // exact matched
            poll(this.marketAsks)
            poll(this.limitBids)
            return MatchResult.exact(mAsk, symbol, lBid)
        } else if (d > 0) { // ask has more shares and need more bid
            val tBids = ArrayList<OrderEntry>()
            tBids.add(poll(this.limitBids)!!)
            while (d > 0) {
                val peek = peek(this.limitBids)
                if (peek == null) {
                    this.limitBids.addAll(tBids)
                    return null
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mAsk, d)
                    tBids.add(peek)
                    poll(this.marketBids)
                    d = 0
                } else if (d > peek.shares()) {
                    d -= peek.shares()
                    tBids.add(poll(this.limitBids)!!)
                } else { // d == peek.shares()
                    tBids.add(poll(this.limitBids)!!)
                    break
                }
            }

            return MatchResult.bigBid(mAsk, symbol, tBids)
        } else { // bid has more shares and need more ask
            lBid.partialMatched(mAsk)
            poll(this.marketAsks)
            return MatchResult.bigAsk(mAsk, symbol, lBid)
        }
    }

    private fun peek(queue: SortedSet<OrderEntry>): OrderEntry? {
        return try {
            queue.first()
        } catch (e: NoSuchElementException) {
            null
        }
    }

    private fun poll(queue: SortedSet<OrderEntry>): OrderEntry? {
        return try {
            val first = queue.first()
            queue.remove(first)
            first
        } catch (e: NoSuchElementException) {
            null
        }
    }

}