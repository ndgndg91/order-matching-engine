package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

data class ChannelResult(
    val symbol: Symbol,
    val price: BigDecimal,
    val timestamp: LocalDateTime
) {
    constructor(matchResult: MatchResult): this(
        matchResult.symbol,
        matchResult.averagePrice(),
        matchResult.matchedAt
    )
}