package com.ndgndg91.ordersimulator.client.request

data class AddOrderRequest(
    val orderType: String,
    val symbol: String,
    val shares: String,
    val priceType: String,
    val price: String
)
