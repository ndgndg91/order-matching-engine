package com.ndgndg91.ordermatchingenginekotlin.order.event

import com.ndgndg91.ordermatchingenginekotlin.order.OrderType
import com.ndgndg91.ordermatchingenginekotlin.order.PriceType
import com.ndgndg91.ordermatchingenginekotlin.order.Symbol
import java.time.LocalDateTime

data class OrderMatchTriggerEvent(
    val symbol: Symbol,
    val priceType: PriceType,
    val orderType: OrderType,
    val creationTime: LocalDateTime = LocalDateTime.now()
)
