package com.ndgndg91.ordermatchingenginekotlin.order.service

import com.ndgndg91.ordermatchingenginekotlin.global.OrderServiceException
import com.ndgndg91.ordermatchingenginekotlin.order.*
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap

@Service
class OrderBooks {
    companion object {
        const val MESSAGE = "Not Found Symbol : %s \t OrderId : %s"
    }

    private val m: ConcurrentHashMap<Symbol, OrderBook> = ConcurrentHashMap(Symbol.values().associateWith { OrderBook(it) })

    fun addOrder(order: Order) {
        val orderBook = m[order.symbol]
        if (kotlin.runCatching { orderBook!!.addOrder(order) }.isFailure) {
            throw OrderServiceException(
                null,
                HttpStatus.BAD_REQUEST.value(),
                String.format(MESSAGE, order.symbol, order.orderId)
            )
        }
    }

    fun modifyOrder(order: Order) {
        val orderBook = m[order.symbol]
        if (kotlin.runCatching { orderBook!!.modifyOrder(order) }.isFailure) {
            throw OrderServiceException(
                null,
                HttpStatus.BAD_REQUEST.value(),
                String.format(MESSAGE, order.symbol, order.orderId)
            )
        }
    }

    fun cancelOrder(order: Order) {
        val orderBook = m[order.symbol]
        if (kotlin.runCatching { orderBook!!.cancelOrder(order) }.isFailure) {
            throw OrderServiceException(
                null,
                HttpStatus.BAD_REQUEST.value(),
                String.format(MESSAGE, order.symbol, order.orderId)
            )
        }
    }

    fun find(symbol: Symbol, orderType: OrderType, orderId: String): OrderEntry? {
        val orderBook = m[symbol]
        val findResult = kotlin.runCatching { orderBook!!.find(orderType, orderId) }
        if (findResult.isFailure) {
            throw OrderServiceException(
                null,
                HttpStatus.BAD_REQUEST.value(),
                String.format(MESSAGE, symbol, orderId)
            )
        }

        return findResult.getOrNull()
    }

    fun match(symbol: Symbol, priceType: PriceType, orderType: OrderType): List<MatchResult> {
        val orderBook = m[symbol]
        return orderBook!!.match(priceType, orderType)
    }
}