package com.ndgndg91.ordermatchingenginekotlin.order.dto.response

import com.ndgndg91.ordermatchingenginekotlin.order.OrderEntry
import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderEntryResponse(private val e: OrderEntry) {
    val orderId: String = e.orderId
    val shares: Int = e.shares
    val priceType: String = e.priceType.name
    val price: BigDecimal = e.price
    val timestamp: LocalDateTime = e.timestamp
    val partialMatched: Boolean = e.partialMatched
    val currentShares: Int = e.shares()
}

