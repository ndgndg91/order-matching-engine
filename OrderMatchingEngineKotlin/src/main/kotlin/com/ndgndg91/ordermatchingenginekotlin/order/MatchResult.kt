package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

class MatchResult(
    val symbol: Symbol,
    val orderId: String,
    val orderType: OrderType,
    val shares: Int,
    val priceType: PriceType,
    val price: BigDecimal,
    private val timestamp: LocalDateTime,
    var matchedEntries: List<MatchedEntry> = listOf()
) {
    companion object {
        fun bidExactAsk(bid: OrderEntry, symbol: Symbol, ask: OrderEntry): MatchResult = MatchResult(bid.toBidEntry(), symbol, ask.toAskEntry())
        fun bigBidSmallAsks(bid: OrderEntry, symbol: Symbol, tAsks: List<OrderEntry>): MatchResult = MatchResult(bid.toBidEntry(), symbol, tAsks.map { it.toAskEntry() })
        fun smallBidBigAsk(bid: OrderEntry, symbol: Symbol, ask: OrderEntry): MatchResult = MatchResult(bid.toBidEntry(), symbol, ask.toAskEntry())

        fun askExactBid(ask: OrderEntry, symbol: Symbol, bid: OrderEntry): MatchResult = MatchResult(ask.toAskEntry(), symbol, bid.toBidEntry())
        fun bigAskSmallBids(ask: OrderEntry, symbol: Symbol, tBids: List<OrderEntry>): MatchResult = MatchResult(ask.toAskEntry(), symbol, tBids.map { it.toBidEntry() })
        fun smallAskBigBid(ask: OrderEntry, symbol: Symbol, bid: OrderEntry): MatchResult = MatchResult(ask.toAskEntry(), symbol, bid.toBidEntry())
    }

    private constructor(ask: AskOrderEntry, symbol: Symbol, bid: BidOrderEntry): this(
        symbol = symbol,
        orderId = ask.orderId,
        orderType = ask.orderType,
        shares = if (ask.partialMatched) ask.shares() else ask.shares,
        priceType = ask.priceType,
        price = ask.price,
        timestamp = ask.timestamp,
        matchedEntries = listOf(MatchedEntry(ask, bid.shares()))
    )

    private constructor(ask: AskOrderEntry, symbol: Symbol, bids: List<BidOrderEntry>): this(
        symbol = symbol,
        orderId = ask.orderId,
        orderType = ask.orderType,
        shares = if (ask.partialMatched) ask.shares() else ask.shares,
        priceType = ask.priceType,
        price = ask.price,
        timestamp = ask.timestamp,
        matchedEntries = bids.map { MatchedEntry(it, ask.orderId)}
    )

    private constructor(bid: BidOrderEntry, symbol: Symbol, ask: AskOrderEntry): this(
        symbol = symbol,
        orderId = bid.orderId,
        orderType = bid.orderType,
        shares = if (bid.partialMatched) bid.shares() else bid.shares,
        priceType = bid.priceType,
        price = bid.price,
        timestamp = bid.timestamp,
        matchedEntries = listOf(MatchedEntry(ask, bid.shares()))
    )

    private constructor(bid: BidOrderEntry, symbol: Symbol, asks: List<AskOrderEntry>): this(
        symbol = symbol,
        orderId = bid.orderId,
        orderType = bid.orderType,
        shares = if (bid.partialMatched) bid.shares() else bid.shares,
        priceType = bid.priceType,
        price = bid.price,
        timestamp = bid.timestamp,
        matchedEntries = asks.map { MatchedEntry(it, bid.orderId) }
    )

    fun matchedShare(): Int = this.matchedEntries.stream().mapToInt(MatchedEntry::shares).sum()

    fun averagePrice(): BigDecimal = this.matchedEntries.stream()
        .map { it.totalPrice() }.reduce(BigDecimal.ZERO, BigDecimal::add)
        .divide(BigDecimal(this.shares), RoundingMode.CEILING)

    override fun toString(): String {
        return "MatchResult(symbol=$symbol, orderId='$orderId', orderType=$orderType, shares=$shares, priceType=$priceType, price=$price, timestamp=$timestamp, matchedEntries=$matchedEntries)"
    }


}
