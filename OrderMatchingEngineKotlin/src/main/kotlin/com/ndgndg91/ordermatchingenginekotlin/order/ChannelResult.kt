package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.math.BigInteger
import java.time.ZoneOffset

data class ChannelResult(
    val symbol: Symbol,
    val price: BigDecimal,
    val timestamp: BigInteger
) {
    constructor(matchResult: MatchResult): this(
        matchResult.symbol,
        matchResult.averagePrice(),
        matchResult.matchedAt.toEpochSecond(ZoneOffset.UTC).toBigInteger()
    )
}