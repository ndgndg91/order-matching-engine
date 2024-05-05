package com.ndgndg91.ordermatchingenginekotlin.order.dto.request

import com.ndgndg91.ordermatchingenginekotlin.order.validation.OrderTypeValue
import com.ndgndg91.ordermatchingenginekotlin.order.validation.SymbolValue
import javax.validation.constraints.NotNull

data class CancelOrderRequest(
    @field:NotNull(message = "order id is required") val orderId: String?,
    @field:OrderTypeValue @field:NotNull(message = "order type is required") val orderType: String?,
    @field:SymbolValue @field:NotNull(message = "symbol is required") val symbol: String?
)
