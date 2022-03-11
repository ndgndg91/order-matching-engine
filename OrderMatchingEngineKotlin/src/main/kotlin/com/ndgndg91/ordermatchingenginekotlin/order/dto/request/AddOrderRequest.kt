package com.ndgndg91.ordermatchingenginekotlin.order.dto.request

import java.math.BigDecimal

data class AddOrderRequest(
    val orderType: String,
    val symbol: String,
    val shares: Int,
    val priceType: String,
    val price: BigDecimal
)
