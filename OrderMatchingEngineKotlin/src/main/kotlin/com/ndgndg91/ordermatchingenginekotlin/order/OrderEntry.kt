package com.ndgndg91.ordermatchingenginekotlin.order

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderEntry(
    val order: Order,

) {
    val orderId: String = order.orderId
    val orderType: OrderType = order.orderType
    val shares: Int = order.shares
    val priceType: PriceType = order.priceType
    val price: BigDecimal = order.price
    val timestamp: LocalDateTime = order.timestamp
    var partialMatched: Boolean = false
    val partialMatchedEntries: MutableList<PartialMatched> = mutableListOf()

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
        this.partialMatchedEntries.add(pm)
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
}