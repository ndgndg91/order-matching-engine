package com.ndgndg91.ordermatchingenginekotlin.order.dto.request

data class CancelOrderRequest(
    val orderId: String,
    val orderType: String,
    val symbol: String
)
