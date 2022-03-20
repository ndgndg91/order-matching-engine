package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import kotlin.streams.toList

class MatchResult {
    val symbol: Symbol
    val orderId: String
    val orderType: OrderType
    val shares: Int
    val priceType: PriceType
    val price: BigDecimal
    private val timestamp: LocalDateTime
    var matchedEntries: List<MatchedEntry> = mutableListOf()

    companion object {
        fun bidExactAsk(bid: OrderEntry, symbol: Symbol, ask: OrderEntry): MatchResult = MatchResult(bid, symbol, ask)
        fun bigBidSmallAsks(bid: OrderEntry, symbol: Symbol, tAsks: List<OrderEntry>): MatchResult = MatchResult(bid, symbol, tAsks)
        fun smallBidBigAsk(bid: OrderEntry, symbol: Symbol, ask: OrderEntry): MatchResult = MatchResult(bid, symbol, ask)

        fun askExactBid(ask: OrderEntry, symbol: Symbol, bid: OrderEntry): MatchResult = MatchResult(ask, symbol, bid)
        fun bigAskSmallBids(ask: OrderEntry, symbol: Symbol, tBids: List<OrderEntry>): MatchResult = MatchResult(ask, symbol, tBids)
        fun smallAskBigBid(ask: OrderEntry, symbol: Symbol, bid: OrderEntry): MatchResult = MatchResult(ask, symbol, bid)
    }

    private constructor(bid: OrderEntry, symbol: Symbol, ask: OrderEntry) {
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

    fun matchedShare(): Int = this.matchedEntries.stream().mapToInt(MatchedEntry::shares).sum()

    fun averagePrice(): BigDecimal = this.matchedEntries.stream()
        .map { it.totalPrice() }.reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal(this.shares), RoundingMode.CEILING)

    override fun toString(): String {
        return "MatchResult(symbol=$symbol, orderId='$orderId', orderType=$orderType, shares=$shares, priceType=$priceType, price=$price, timestamp=$timestamp, matchedEntries=$matchedEntries)"
    }


}
