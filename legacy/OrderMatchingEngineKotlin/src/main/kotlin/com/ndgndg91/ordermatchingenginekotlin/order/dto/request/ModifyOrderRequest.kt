package com.ndgndg91.ordermatchingenginekotlin.order.dto.request

import com.ndgndg91.ordermatchingenginekotlin.order.validation.OrderTypeValue
import com.ndgndg91.ordermatchingenginekotlin.order.validation.PriceTypeValue
import com.ndgndg91.ordermatchingenginekotlin.order.validation.SymbolValue
import java.math.BigDecimal
import javax.validation.constraints.*

data class ModifyOrderRequest(
    @field:NotNull(message = "order id is required") val orderId: String?,
    @field:OrderTypeValue @field:NotNull(message = "order type is required") val orderType: String?,
    @field:SymbolValue @field:NotNull(message = "symbol is required") val symbol: String?,
    @field:Min(1) @field:Max(Int.MAX_VALUE.toLong()) @field:NotNull(message = "shares is required") val shares: Int?,
    @field:PriceTypeValue @field:NotNull(message = "price type is required") val priceType: String?,
    @field:DecimalMin("0.0") @field:DecimalMax("9999999999.99") @field:Digits(integer = 10, fraction = 2)
    @field:NotNull(message = "price is required") val price: BigDecimal?
)