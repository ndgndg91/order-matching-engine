package com.ndgndg91.ordermatchingenginekotlin.order

import java.util.*
import java.util.concurrent.LinkedBlockingDeque

class OrderBook(private val symbol: Symbol) {
    private val limitBids: Queue<OrderEntry> = PriorityQueue(200) { e1, e2 ->
        val c = e2.price.compareTo(e1.price)
        if (c == 0) e1.timestamp.compareTo(e2.timestamp) else c
    }

    private val limitAsks: Queue<OrderEntry> = PriorityQueue(200) { e1, e2 ->
        val c = e1.price.compareTo(e2.price)
        if (c == 0) e1.timestamp.compareTo(e2.timestamp) else c
    }
    private val marketBids: Queue<OrderEntry> = LinkedBlockingDeque()
    private val marketAsks: Queue<OrderEntry> = LinkedBlockingDeque()

    private fun selectQueue(priceType: PriceType, orderType: OrderType): Queue<OrderEntry> {
        return when (priceType) {
            PriceType.MARKET -> marketQueue(orderType)
            PriceType.LIMIT -> limitQueue(orderType)
        }
    }

    private fun marketQueue(orderType: OrderType): Queue<OrderEntry> {
        return when (orderType) {
            OrderType.BID -> this.marketBids
            OrderType.ASK -> this.marketAsks
        }
    }

    private fun limitQueue(orderType: OrderType): Queue<OrderEntry> {
        return when (orderType) {
            OrderType.BID -> this.limitBids
            OrderType.ASK -> this.limitAsks
        }
    }

    fun addOrder(order: Order) {
        val e = OrderEntry(order)
        selectQueue(order.priceType, order.orderType).add(e)
    }

    fun modifyOrder(order: Order) {
        val queue = selectQueue(order.priceType, order.orderType)
        val e = kotlin.runCatching { queue.first { it.orderId == order.orderId } }
            .onSuccess { queue.remove(it) }

        queue.add(OrderEntry(order))
    }

    fun cancelOrder(order: Order) {
        val queue = selectQueue(order.priceType, order.orderType)

        kotlin.runCatching { queue.first { it.orderId == order.orderId } }
            .onSuccess { queue.remove(it) }
    }

    fun find(orderType: OrderType, orderId: String): OrderEntry? {
        when(orderType) {
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

}