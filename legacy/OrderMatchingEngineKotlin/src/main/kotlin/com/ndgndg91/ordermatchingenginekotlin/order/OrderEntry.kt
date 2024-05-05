package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

open class OrderEntry(
    open val orderId: String,
    open val orderType: OrderType,
    open val shares: Int,
    open val priceType: PriceType,
    open val price: BigDecimal,
    open val timestamp: LocalDateTime,
    open var partialMatched: Boolean,
    open var partialMatchedEntries: List<PartialMatched>) {

    fun toBidEntry(): BidOrderEntry = BidOrderEntry(
        orderId,
        orderType,
        shares,
        priceType,
        price,
        timestamp,
        partialMatched,
        partialMatchedEntries
    )

    fun toAskEntry(): AskOrderEntry = AskOrderEntry(
        orderId,
        orderType,
        shares,
        priceType,
        price,
        timestamp,
        partialMatched,
        partialMatchedEntries
    )


    fun partialMatched(e: OrderEntry) {
        this.partialMatched = true
        val pm = PartialMatched(
            e.orderId,
            e.orderType,
            e.shares(),
            e.priceType,
            e.price,
            e.timestamp
        )
        this.partialMatchedEntries = this.partialMatchedEntries.plus(pm)
    }

    fun partialMatched(e: OrderEntry, shares: Int) {
        this.partialMatched = true
        val pm = PartialMatched(e.orderId, e.orderType, shares, e.priceType, e.price, e.timestamp)
        this.partialMatchedEntries = this.partialMatchedEntries.plus(pm)
    }

    fun shares(): Int {
        return if (partialMatched) {
            this.shares - partialMatchedEntries.asSequence().map { it.shares }.sum()
        } else {
            this.shares
        }
    }

    fun partialShares(orderId: String): Int {
        val sum = this.partialMatchedEntries.asSequence()
            .filter { it.orderId == orderId }
            .map { it.shares }
            .sum()

        return if (sum > 0) sum else this.shares - partialMatchedEntries.asSequence()
            .map { it.shares }
            .sum()
    }


    data class PartialMatched(
        val orderId: String,
        val orderType: OrderType,
        val shares: Int,
        val priceType: PriceType,
        val price: BigDecimal,
        val timestamp: LocalDateTime
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as OrderEntry

        if (orderId != other.orderId) return false

        return true
    }

    override fun hashCode(): Int {
        return orderId.hashCode()
    }

    override fun toString(): String {
        return "OrderEntry(orderId='$orderId', orderType=$orderType, shares=$shares, priceType=$priceType, price=$price, timestamp=$timestamp, partialMatched=$partialMatched, partialMatchedEntries=$partialMatchedEntries)"
    }
}