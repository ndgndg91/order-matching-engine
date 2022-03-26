package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

class BidOrderEntry(
    override val orderId: String,
    override val orderType: OrderType,
    override val shares: Int,
    override val priceType: PriceType,
    override val price: BigDecimal,
    override val timestamp: LocalDateTime,
    override var partialMatched: Boolean,
    override val partialMatchedEntries: MutableList<PartialMatched>
) : OrderEntry(orderId, orderType, shares, priceType, price, timestamp, partialMatched, partialMatchedEntries)