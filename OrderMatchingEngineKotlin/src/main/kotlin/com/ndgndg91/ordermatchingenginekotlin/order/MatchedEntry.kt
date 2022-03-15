package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

class MatchedEntry {
    val orderId: String
    val orderType: OrderType
    val shares: Int
    val priceType: PriceType
    val price: BigDecimal
    val timestamp: LocalDateTime

    constructor(entry: OrderEntry, s: Int) {
        this.orderId = entry.orderId
        this.orderType = entry.orderType
        this.shares = s
        this.priceType = entry.priceType
        this.price = entry.price
        this.timestamp = entry.timestamp
    }

    constructor(entry: OrderEntry, orderId: String) {
        this.orderId = entry.orderId
        this.orderType = entry.orderType
        this.shares = entry.partialShares(orderId)
        this.priceType = entry.priceType
        this.price = entry.price
        this.timestamp = entry.timestamp
    }

    fun totalPrice(): BigDecimal {
        return this.price.multiply(BigDecimal(this.shares))
    }
}

