package com.ndgndg91.ordermatchedsse

import java.math.BigDecimal
import java.math.BigInteger

data class ChannelResult(
    val symbol: Symbol,
    val price: BigDecimal,
    val timestamp: BigInteger
)