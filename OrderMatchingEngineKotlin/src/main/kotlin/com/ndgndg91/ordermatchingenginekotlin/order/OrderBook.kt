package com.ndgndg91.ordermatchingenginekotlin.order

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.*
import java.util.concurrent.ConcurrentSkipListSet

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

    fun find(orderType: OrderType, orderId: String): OrderEntry? {
        when (orderType) {
            OrderType.ASK -> {
                val inLimitAsks = kotlin.runCatching { this.limitAsks.first { it.orderId == orderId } }
                if (inLimitAsks.isSuccess) {
                    return inLimitAsks.getOrThrow()
                }

                val inMarketAsks = kotlin.runCatching { this.marketAsks.first { it.orderId == orderId } }
                if (inMarketAsks.isSuccess) {
                    return inMarketAsks.getOrThrow()
                }

                return null
            }
            OrderType.BID -> {
                val inLimitBids = kotlin.runCatching { this.limitBids.first { it.orderId == orderId } }
                if (inLimitBids.isSuccess) {
                    return inLimitBids.getOrThrow()
                }


                val inMarketBids = kotlin.runCatching { this.marketBids.first { it.orderId == orderId } }
                if (inMarketBids.isSuccess) {
                    return inMarketBids.getOrThrow()
                }

                return null
            }
        }
    }

    fun bidsPoll(): OrderEntry? {
        return if (!this.marketBids.isEmpty()) {
            val first = this.marketBids.first()
            this.marketBids.remove(first)
            first
        } else {
            val first = this.limitBids.first()
            this.limitBids.remove(first)
            first
        }
    }

    fun asksPoll(): OrderEntry? {
        return if (!this.marketAsks.isEmpty()) {
            val first = this.marketAsks.first()
            this.marketAsks.remove(first)
            first
        } else {
            val first = this.limitAsks.first()
            this.limitAsks.remove(first)
            first
        }
    }

    fun match(priceType: PriceType, orderType: OrderType): MatchResult {
        return when(priceType) {
            PriceType.LIMIT -> matchLimitOrder()
            PriceType.MARKET -> matchMarketOrder(orderType)
        }
    }

    fun matchLimitOrder(): MatchResult {
        TODO("Not yet implemented")
    }

    fun matchMarketOrder(orderType: OrderType): MatchResult {
        return when(orderType) {
            OrderType.BID -> matchMarketBidOrder()
            OrderType.ASK -> matchMarketAskOrder()
        }
    }

    private fun matchMarketAskOrder(): MatchResult {
        TODO("Not yet implemented")
    }

    private fun matchMarketBidOrder(): MatchResult {
        TODO("Not yet implemented")
    }

}