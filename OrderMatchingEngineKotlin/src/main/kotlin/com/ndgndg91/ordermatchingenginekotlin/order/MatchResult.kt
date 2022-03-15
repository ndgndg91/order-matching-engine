package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

class MatchResult {
    val symbol: Symbol
    val orderId: String
    val orderType: OrderType
    val shares: Int
    val priceType: PriceType
    val price: BigDecimal
    val timestamp: LocalDateTime
    var matchedEntries: List<MatchedEntry> = mutableListOf()

    // TODO: exact, bigAsk, bigBid 정적 팩토리 메서드

    constructor(bid: OrderEntry, symbol: Symbol, ask: OrderEntry) {
        this.symbol = symbol
        this.orderId = bid.orderId
        this.orderType = bid.orderType
        this.shares = if (bid.partialMatched) bid.shares() else bid.shares
        this.priceType = bid.priceType
        this.price = bid.price
        this.timestamp = bid.timestamp
        this.matchedEntries = this.matchedEntries.plus(MatchedEntry(ask, bid.shares()))
    }

    private constructor(bid: OrderEntry, symbol: Symbol, asks: List<OrderEntry>) {
        this.symbol = symbol
        this.orderId = bid.orderId
        this.orderType = bid.orderType
        this.shares = if (bid.partialMatched) bid.shares() else bid.shares
        this.priceType = bid.priceType
        this.price = bid.price
        this.timestamp = bid.timestamp
        val entries = asks.stream().map { MatchedEntry(it, bid.orderId) }.toList()
        this.matchedEntries = this.matchedEntries.plus(entries)
    }

    // TODO: matchedShare() function, averagePrice function
}
