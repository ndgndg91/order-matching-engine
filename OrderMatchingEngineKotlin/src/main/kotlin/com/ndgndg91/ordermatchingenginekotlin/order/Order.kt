package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

class Order private constructor(
    val orderId: String,
    val orderType: OrderType,
    val symbol: Symbol,
    val shares: Int,
    val price: BigDecimal,
    val priceType: PriceType,
    val timestamp: LocalDateTime
){
    fun toOrderEntry(): OrderEntry = OrderEntry(
        orderId = orderId,
        orderType = orderType,
        shares = shares,
        priceType = priceType,
        price = price,
        timestamp = timestamp,
        partialMatched = false,
        partialMatchedEntries = listOf()
    )

    data class Builder(
        private var orderId: String? = null,
        private var orderType: OrderType? = null,
        private var symbol: Symbol? = null,
        private var shares: Int? = null,
        private var price: BigDecimal? = null,
        private var priceType: PriceType? = null,
        private var timestamp: LocalDateTime = LocalDateTime.now()
    ) {
        fun orderId(id: String) = apply { this.orderId = id }
        fun orderType(type: OrderType) = apply { this.orderType = type }
        fun symbol(symbol: Symbol) = apply { this.symbol = symbol }
        fun shares(shares: Int) = apply { this.shares = shares }
        fun price(price: BigDecimal) = apply { this.price = price }
        fun priceType(priceType: PriceType) = apply { this.priceType = priceType }
        fun timestamp(timestamp: LocalDateTime) = apply { this.timestamp = timestamp }
        fun build() = Order(orderId!!, orderType!!, symbol!!, shares!!, price!!, priceType!!, timestamp)
    }

    override fun toString(): String {
        return "Order(orderId='$orderId', orderType=$orderType, symbol=$symbol, shares=$shares, price=$price, priceType=$priceType, timestamp=$timestamp)"
    }


}