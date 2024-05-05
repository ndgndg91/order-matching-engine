package com.ndgndg91.ordermatchingenginekotlin.order.service

import com.ndgndg91.ordermatchingenginekotlin.order.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime
import java.util.*

internal class OrderBooksTest {

    private val log: Logger = LoggerFactory.getLogger(OrderBooksTest::class.java)

    @Test
    fun addBids() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val bid1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .price(BigDecimal(5000))
            .priceType(PriceType.LIMIT)
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        val bid2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .price(BigDecimal(15000))
            .priceType(PriceType.LIMIT)
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()

        val bid3 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .price(BigDecimal(7000))
            .priceType(PriceType.MARKET)
            .shares(500)
            .symbol(Symbol.AAPL)
            .build()

        val bid4 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .price(BigDecimal(9000))
            .priceType(PriceType.LIMIT)
            .shares(300)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(bid1)
        orderBook.addOrder(bid2)
        orderBook.addOrder(bid3)
        orderBook.addOrder(bid4)

        val bidsPoll1 = orderBook.bidsPoll()
        val bidsPoll2 = orderBook.bidsPoll()
        val bidsPoll3 = orderBook.bidsPoll()
        val bidsPoll4 = orderBook.bidsPoll()

        // then
        Assertions.assertThat(bidsPoll1!!.orderId).isEqualTo(bid3.orderId)
        Assertions.assertThat(bidsPoll2!!.orderId).isEqualTo(bid2.orderId)
        Assertions.assertThat(bidsPoll3!!.orderId).isEqualTo(bid4.orderId)
        Assertions.assertThat(bidsPoll4!!.orderId).isEqualTo(bid1.orderId)
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
    }

    @Test
    fun addAsks() {
        val orderBook = OrderBook(Symbol.AAPL)
        val ask1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .price(BigDecimal(5000))
            .priceType(PriceType.LIMIT)
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        val ask2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .price(BigDecimal(15000))
            .priceType(PriceType.LIMIT)
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()

        val ask3 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .price(BigDecimal(7000))
            .priceType(PriceType.MARKET)
            .shares(500)
            .symbol(Symbol.AAPL)
            .build()

        val ask4 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .price(BigDecimal(9000))
            .priceType(PriceType.LIMIT)
            .shares(300)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(ask1)
        orderBook.addOrder(ask2)
        orderBook.addOrder(ask3)
        orderBook.addOrder(ask4)

        val asksPoll1 = orderBook.asksPoll()
        val asksPoll2 = orderBook.asksPoll()
        val asksPoll3 = orderBook.asksPoll()
        val asksPoll4 = orderBook.asksPoll()

        // then
        Assertions.assertThat(asksPoll1!!.orderId).isEqualTo(ask3.orderId)
        Assertions.assertThat(asksPoll2!!.orderId).isEqualTo(ask1.orderId)
        Assertions.assertThat(asksPoll3!!.orderId).isEqualTo(ask4.orderId)
        Assertions.assertThat(asksPoll4!!.orderId).isEqualTo(ask2.orderId)
        Assertions.assertThat(orderBook.asksPoll()).isNull()
    }

    @Test
    fun modifyLimitAskOrder() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val id = UUID.randomUUID().toString()
        val order = Order.Builder()
            .orderId(id)
            .orderType(OrderType.ASK)
            .price(BigDecimal(10_000))
            .priceType(PriceType.LIMIT)
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(order)

        val mOrder = Order.Builder()
            .orderId(id)
            .orderType(OrderType.ASK)
            .price(BigDecimal(15_000))
            .priceType(PriceType.LIMIT)
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()

        orderBook.modifyOrder(mOrder)
        val asksPoll = orderBook.asksPoll()

        // then
        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(id)
        Assertions.assertThat(asksPoll.price).isEqualTo(BigDecimal(15_000))
        Assertions.assertThat(asksPoll.shares).isEqualTo(200)
        Assertions.assertThat(orderBook.asksPoll()).isNull()
    }

    @Test
    fun modifyMarketAskOrder() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val id = UUID.randomUUID().toString()
        val order = Order.Builder()
            .orderId(id)
            .orderType(OrderType.ASK)
            .price(BigDecimal(10_000))
            .priceType(PriceType.MARKET)
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(order)

        val mOrder = Order.Builder()
            .orderId(id)
            .orderType(OrderType.ASK)
            .price(BigDecimal(20_000))
            .priceType(PriceType.MARKET)
            .shares(500)
            .symbol(Symbol.AAPL)
            .build()

        orderBook.modifyOrder(mOrder)
        val asksPoll = orderBook.asksPoll()

        // then
        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(id)
        Assertions.assertThat(asksPoll.price).isEqualTo(BigDecimal(20_000))
        Assertions.assertThat(asksPoll.shares).isEqualTo(500)
        Assertions.assertThat(orderBook.asksPoll()).isNull()
    }

    @Test
    fun modifyLimitBidOrder() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val id = UUID.randomUUID().toString()
        val order = Order.Builder()
            .orderId(id)
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(order)

        val mOrder = Order.Builder()
            .orderId(id)
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(25_000))
            .shares(300)
            .symbol(Symbol.AAPL)
            .build()

        orderBook.modifyOrder(mOrder)
        val bidsPoll = orderBook.bidsPoll()

        // then
        Assertions.assertThat(bidsPoll!!.orderId).isEqualTo(id)
        Assertions.assertThat(bidsPoll.price).isEqualTo(BigDecimal(25_000))
        Assertions.assertThat(bidsPoll.shares()).isEqualTo(300)
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
    }

    @Test
    fun modifyMarketBidOrder() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val id = UUID.randomUUID().toString()
        val order = Order.Builder()
            .orderId(id)
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal(10_000))
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(order)

        val mOrder = Order.Builder()
            .orderId(id)
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal(25_000))
            .shares(300)
            .symbol(Symbol.AAPL)
            .build()

        orderBook.modifyOrder(mOrder)
        val bidsPoll = orderBook.bidsPoll()

        // then
        Assertions.assertThat(bidsPoll!!.orderId).isEqualTo(id)
        Assertions.assertThat(bidsPoll.price).isEqualTo(BigDecimal(25_000))
        Assertions.assertThat(bidsPoll.shares()).isEqualTo(300)
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
    }

    @Test
    fun cancelAsks() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val order = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .shares(100)
            .symbol(Symbol.AAPL)
            .build()
        orderBook.addOrder(order)

        // when
        orderBook.cancelOrder(order)

        // then
        Assertions.assertThat(orderBook.asksPoll()).isNull()
    }

    @Test
    fun cancelBids() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val order = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()
        orderBook.addOrder(order)

        // when
        orderBook.cancelOrder(order)

        // then
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
    }

    @Test
    fun emptyMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        // when
        val match = orderBook.match(PriceType.LIMIT, OrderType.BID)

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(0)
    }

    @Test
    fun onlyBidMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val bid1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal(100_000))
            .symbol(Symbol.AAPL)
            .shares(150)
            .build()

        val bid2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(100_001))
            .symbol(Symbol.AAPL)
            .shares(250)
            .build()
        orderBook.addOrder(bid1)
        orderBook.addOrder(bid2)

        // when
        val match = orderBook.match(PriceType.MARKET, OrderType.BID)

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(0)
    }

    @Test
    fun onlyAskMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val ask1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.MARKET)
            .price(BigDecimal(100_000))
            .symbol(Symbol.AAPL)
            .shares(150)
            .build()

        val ask2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(100_001))
            .symbol(Symbol.AAPL)
            .shares(250)
            .build()
        orderBook.addOrder(ask1)
        orderBook.addOrder(ask2)

        // when
        val match = orderBook.match(PriceType.MARKET, OrderType.ASK)

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(0)
    }

    @Test
    fun limitExactMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val limitBid = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(15_000))
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()
        val limitAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(15_000))
            .shares(200)
            .symbol(Symbol.AAPL)
            .build()
        orderBook.addOrder(limitAsk)
        orderBook.addOrder(limitBid)

        // when
        val match = orderBook.match(PriceType.LIMIT, OrderType.BID)
        log.info("$match")

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
        Assertions.assertThat(orderBook.asksPoll()).isNull()
        Assertions.assertThat(match[0].orderId).isEqualTo(limitBid.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(limitBid.shares)
        Assertions.assertThat(match[0].price).isEqualTo(limitBid.price)
        Assertions.assertThat(match[0].matchedShare()).isEqualTo(limitBid.shares)
        Assertions.assertThat(match[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(limitAsk.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(limitAsk.price)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(limitAsk.shares)
    }

    @Test
    fun bigAskLimitMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val bigAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(20_000))
            .shares(2500)
            .symbol(Symbol.AAPL)
            .build()

        val smallBid1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(20_000))
            .shares(500)
            .symbol(Symbol.AAPL)
            .build()

        val smallBid2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(20_000))
            .shares(1000)
            .symbol(Symbol.AAPL)
            .build()
        orderBook.addOrder(bigAsk)
        orderBook.addOrder(smallBid1)
        orderBook.addOrder(smallBid2)

        // when
        val match1 = orderBook.match(PriceType.LIMIT, OrderType.BID)
        val match2 = orderBook.match(PriceType.LIMIT, OrderType.BID)

        // then
        Assertions.assertThat(match1).hasSize(1)
        Assertions.assertThat(match1[0].orderId).isEqualTo(smallBid1.orderId)
        Assertions.assertThat(match1[0].price).isEqualTo(smallBid1.price)
        Assertions.assertThat(match1[0].shares).isEqualTo(smallBid1.shares)
        Assertions.assertThat(match1[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match1[0].matchedEntries[0].orderId).isEqualTo(bigAsk.orderId)
        Assertions.assertThat(match1[0].matchedEntries[0].price).isEqualTo(bigAsk.price)
        Assertions.assertThat(match1[0].matchedEntries[0].shares).isEqualTo(smallBid1.shares)

        Assertions.assertThat(match2).hasSize(1)
        Assertions.assertThat(match2[0].orderId).isEqualTo(smallBid2.orderId)
        Assertions.assertThat(match2[0].price).isEqualTo(smallBid2.price)
        Assertions.assertThat(match2[0].shares).isEqualTo(smallBid2.shares)
        Assertions.assertThat(match2[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match2[0].matchedEntries[0].orderId).isEqualTo(bigAsk.orderId)
        Assertions.assertThat(match2[0].matchedEntries[0].price).isEqualTo(bigAsk.price)
        Assertions.assertThat(match2[0].matchedEntries[0].shares).isEqualTo(smallBid2.shares)
//
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
        val asksPoll = orderBook.asksPoll()
        Assertions.assertThat(asksPoll).isNotNull
        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(bigAsk.orderId)
        Assertions.assertThat(asksPoll.price).isEqualTo(bigAsk.price)
        Assertions.assertThat(asksPoll.shares()).isEqualTo(bigAsk.shares - match1[0].shares - match2[0].shares)
        Assertions.assertThat(asksPoll.partialMatched).isTrue
        Assertions.assertThat(asksPoll.partialMatchedEntries).hasSize(2)
        Assertions.assertThat(asksPoll.partialMatchedEntries[0].orderId).isEqualTo(smallBid1.orderId)
        Assertions.assertThat(asksPoll.partialMatchedEntries[0].price).isEqualTo(smallBid1.price)
        Assertions.assertThat(asksPoll.partialMatchedEntries[0].shares).isEqualTo(smallBid1.shares)
        Assertions.assertThat(asksPoll.partialMatchedEntries[1].orderId).isEqualTo(smallBid2.orderId)
        Assertions.assertThat(asksPoll.partialMatchedEntries[1].price).isEqualTo(smallBid2.price)
        Assertions.assertThat(asksPoll.partialMatchedEntries[1].shares).isEqualTo(smallBid2.shares)
    }

    @Test
    fun bigBidLimitMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bigBidLimit = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(50_000))
            .shares(2500)
            .symbol(Symbol.AAPL)
            .build()

        val smallAskLimit1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(50_000))
            .shares(500)
            .symbol(Symbol.AAPL)
            .build()

        val smallAskLimit2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(50_000))
            .shares(1500)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(bigBidLimit)
        orderBook.addOrder(smallAskLimit1)
        orderBook.addOrder(smallAskLimit2)

        val match1 = orderBook.match(PriceType.LIMIT, OrderType.ASK)
        val match2 = orderBook.match(PriceType.LIMIT, OrderType.ASK)
        val bidsPoll = orderBook.bidsPoll()

        log.info("$match1")
        log.info("${match1[0]}")
        log.info("${match1[1]}")
        log.info("$match2")
        log.info("$bidsPoll")

        // then
        Assertions.assertThat(match1).hasSize(2)
        Assertions.assertThat(match2).hasSize(0)
        Assertions.assertThat(bidsPoll).isNotNull

        Assertions.assertThat(match1[0].orderId).isEqualTo(smallAskLimit1.orderId)
        Assertions.assertThat(match1[0].price).isEqualTo(smallAskLimit1.price)
        Assertions.assertThat(match1[0].shares).isEqualTo(smallAskLimit1.shares)

        Assertions.assertThat(match1[1].orderId).isEqualTo(smallAskLimit2.orderId)
        Assertions.assertThat(match1[1].price).isEqualTo(smallAskLimit2.price)
        Assertions.assertThat(match1[1].shares).isEqualTo(smallAskLimit2.shares)

        Assertions.assertThat(bidsPoll!!.orderId).isEqualTo(bigBidLimit.orderId)
        Assertions.assertThat(bidsPoll.shares).isEqualTo(bigBidLimit.shares)
        Assertions.assertThat(bidsPoll.shares()).isEqualTo(500)
    }

    @Test
    fun limitOrderSimulation_1() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bigAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .symbol(Symbol.TSLA)
            .shares(1500)
            .timestamp(LocalDateTime.now())
            .build()

        val smallBid = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .symbol(Symbol.TSLA)
            .shares(500)
            .timestamp(LocalDateTime.now())
            .build()

        val bigBid = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .symbol(Symbol.TSLA)
            .shares(2000)
            .timestamp(LocalDateTime.now())
            .build()

        val lastBigAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .symbol(Symbol.TSLA)
            .shares(1500)
            .timestamp(LocalDateTime.now())
            .build()

        // when
        orderBook.addOrder(bigAsk)
        val match1 = orderBook.match(PriceType.LIMIT, OrderType.BID)

        orderBook.addOrder(smallBid)
        val match2 = orderBook.match(PriceType.LIMIT, OrderType.BID)
        val matchedEntries2 = match2[0].matchedEntries

        orderBook.addOrder(bigBid)
        val match3 = orderBook.match(PriceType.LIMIT, OrderType.BID)

        orderBook.addOrder(lastBigAsk)
        val match4 = orderBook.match(PriceType.LIMIT, OrderType.BID)
        val matchedEntries4 = match4[0].matchedEntries

        val match5 = orderBook.match(PriceType.LIMIT, OrderType.BID)

        val emptyBid = orderBook.bidsPoll()
        val leftAsk = orderBook.asksPoll()

        // then
        Assertions.assertThat(match1).hasSize(0)
        Assertions.assertThat(match2).hasSize(1)
        Assertions.assertThat(match3).hasSize(1)
        Assertions.assertThat(match4).hasSize(1)
        Assertions.assertThat(match5).hasSize(0)

        Assertions.assertThat(matchedEntries2).hasSize(1)
        Assertions.assertThat(matchedEntries4).hasSize(1)

        Assertions.assertThat(emptyBid).isNull()
        Assertions.assertThat(leftAsk).isNotNull
        Assertions.assertThat(leftAsk!!.shares()).isEqualTo(500)
    }

    @Test
    fun limitOrderSimulation_2() {
        // given
        val orderBook = OrderBook(Symbol.MSFT)

        val bid9500 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .price(BigDecimal.valueOf(9500))
            .priceType(PriceType.LIMIT)
            .shares(500)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val bid9600 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(9600))
            .shares(500)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val bid9700 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(9700))
            .shares(500)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val ask9800 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(9800))
            .shares(200)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val ask9900 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(9900))
            .shares(300)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val ask10000 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .shares(600)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        val marketMaker = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(10000))
            .shares(1000)
            .symbol(Symbol.MSFT)
            .timestamp(LocalDateTime.now())
            .build()

        // when
        orderBook.addOrder(bid9500) // 500
        orderBook.addOrder(bid9600) // 500
        orderBook.addOrder(bid9700) // 500

        orderBook.addOrder(ask9800) // 200
        orderBook.addOrder(ask9900) // 300
        orderBook.addOrder(ask10000) // 600

        val match1 = orderBook.match(PriceType.LIMIT, OrderType.BID)
        orderBook.addOrder(marketMaker) // 1000

        val match2 = orderBook.match(PriceType.LIMIT, OrderType.BID)
        val matchedEntries2 = match2[0].matchedEntries
        val sum = match2[0].matchedShare()
        val ask9800Price = ask9800.price.multiply(BigDecimal(ask9800.shares))
        val ask9900Price = ask9900.price.multiply(BigDecimal(ask9900.shares))
        val ask10000Price = ask10000.price.multiply(BigDecimal(500))
        val askSum = ask9800Price.add(ask9900Price).add(ask10000Price)
        val askTotalShares = ask9800.shares + ask9900.shares + 500
        val askAveragePrice = askSum.divide(BigDecimal(askTotalShares), RoundingMode.CEILING)

        val remainAsk = orderBook.asksPoll()
        val remainBid1 = orderBook.bidsPoll()
        val remainBid2 = orderBook.bidsPoll()
        val remainBid3 = orderBook.bidsPoll()

        log.info("$match1")
        log.info("$match2")
        log.info("${match2[0].matchedEntries[0]}")
        log.info("${match2[0].matchedEntries[1]}")
        log.info("${match2[0].matchedEntries[2]}")

        // then
        Assertions.assertThat(match1).hasSize(0)
        Assertions.assertThat(match2).hasSize(1)
        Assertions.assertThat(matchedEntries2).hasSize(3)

        Assertions.assertThat(remainAsk).isNotNull
        Assertions.assertThat(remainAsk!!.shares()).isEqualTo(100)
        Assertions.assertThat(remainBid1).isNotNull
        Assertions.assertThat(remainBid2).isNotNull
        Assertions.assertThat(remainBid3).isNotNull
        Assertions.assertThat(remainBid1!!.orderId).isEqualTo(bid9700.orderId)
        Assertions.assertThat(remainBid2!!.orderId).isEqualTo(bid9600.orderId)
        Assertions.assertThat(remainBid3!!.orderId).isEqualTo(bid9500.orderId)

        Assertions.assertThat(match2[0].orderId).isEqualTo(marketMaker.orderId)
        Assertions.assertThat(match2[0].price).isEqualTo(marketMaker.price)
        Assertions.assertThat(match2[0].shares).isEqualTo(marketMaker.shares)
        Assertions.assertThat(match2[0].shares).isEqualTo(sum)
        Assertions.assertThat(match2[0].averagePrice()).isEqualTo(askAveragePrice)

        Assertions.assertThat(matchedEntries2[0].orderId).isEqualTo(ask9800.orderId)
        Assertions.assertThat(matchedEntries2[1].orderId).isEqualTo(ask9900.orderId)
        Assertions.assertThat(matchedEntries2[2].orderId).isEqualTo(ask10000.orderId)
    }

    @Test
    fun bidMarketEmptyMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val marketBid = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal(10_000))
            .symbol(Symbol.AAPL)
            .shares(500)
            .build()

        // when
        orderBook.addOrder(marketBid)
        val match = orderBook.match(PriceType.MARKET, OrderType.BID)
        val bidsPoll = orderBook.bidsPoll()

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(0)
        Assertions.assertThat(bidsPoll).isNotNull
        Assertions.assertThat(bidsPoll!!.orderId).isEqualTo(marketBid.orderId)
        Assertions.assertThat(bidsPoll.shares).isEqualTo(marketBid.shares)
    }

    @Test
    fun bidMarketExactMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val marketBid = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(500)
            .build()

        val limitAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .symbol(Symbol.AAPL)
            .shares(500)
            .build()

        // when
        orderBook.addOrder(marketBid)
        orderBook.addOrder(limitAsk)
        val match = orderBook.match(PriceType.MARKET, OrderType.BID)

        // then
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(orderBook.bidsPoll()).isNull()
        Assertions.assertThat(orderBook.asksPoll()).isNull()
        Assertions.assertThat(match[0].matchedEntries).hasSize(1)

        Assertions.assertThat(match[0].orderId).isEqualTo(marketBid.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(marketBid.shares)
        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(limitAsk.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(limitAsk.shares)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(limitAsk.price)
    }

    @Test
    fun bidMarketBigBidMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bigBidMarket = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(1500)
            .build()

        val smallLimitAsk1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .symbol(Symbol.AAPL)
            .shares(700)
            .build()

        val smallLimitAsk2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(12_000))
            .symbol(Symbol.AAPL)
            .shares(800)
            .build()

        // when
        orderBook.addOrder(bigBidMarket)
        orderBook.addOrder(smallLimitAsk1)
        orderBook.addOrder(smallLimitAsk2)

        val match = orderBook.match(PriceType.MARKET, OrderType.BID)
        val bidsPoll = orderBook.bidsPoll()
        val asksPoll = orderBook.asksPoll()

        log.info("$match")
        log.info("$bidsPoll")
        log.info("$asksPoll")

        // then
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(bidsPoll).isNull()
        Assertions.assertThat(asksPoll).isNull()

        Assertions.assertThat(match[0].orderId).isEqualTo(bigBidMarket.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(bigBidMarket.shares)
        Assertions.assertThat(match[0].matchedEntries).hasSize(2)
        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(smallLimitAsk1.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(smallLimitAsk1.price)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(smallLimitAsk1.shares)
        Assertions.assertThat(match[0].matchedEntries[1].orderId).isEqualTo(smallLimitAsk2.orderId)
        Assertions.assertThat(match[0].matchedEntries[1].price).isEqualTo(smallLimitAsk2.price)
        Assertions.assertThat(match[0].matchedEntries[1].shares).isEqualTo(smallLimitAsk2.shares)
    }

    @Test
    fun bidMarketBigAskMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bidMarket = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(1500)
            .build()

        val bigAskLimit = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(15_000))
            .symbol(Symbol.AAPL)
            .shares(1800)
            .build()

        // when
        orderBook.addOrder(bidMarket)
        orderBook.addOrder(bigAskLimit)
        val match = orderBook.match(PriceType.MARKET, OrderType.BID)
        val bidsPoll = orderBook.bidsPoll()
        val asksPoll = orderBook.asksPoll()

        // then
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(bidsPoll).isNull()
        Assertions.assertThat(asksPoll).isNotNull
        Assertions.assertThat(match[0].matchedEntries).hasSize(1)

        Assertions.assertThat(match[0].orderId).isEqualTo(bidMarket.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(bidMarket.shares)

        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(bigAskLimit.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(bigAskLimit.price)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(bidMarket.shares)

        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(bigAskLimit.orderId)
        Assertions.assertThat(asksPoll.shares()).isEqualTo(bigAskLimit.shares - bidMarket.shares)
        Assertions.assertThat(asksPoll.price).isEqualTo(bigAskLimit.price)
    }

    @Test
    fun askMarketEmptyMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val marketAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(100)
            .build()

        // when
        orderBook.addOrder(marketAsk)
        val match = orderBook.match(PriceType.MARKET, OrderType.ASK)
        val asksPoll = orderBook.asksPoll()

        // then
        Assertions.assertThat(match).isNotNull
        Assertions.assertThat(match).hasSize(0)
        Assertions.assertThat(asksPoll).isNotNull
        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(marketAsk.orderId)
        Assertions.assertThat(asksPoll.shares).isEqualTo(marketAsk.shares)
    }

    @Test
    fun askMarketExactMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val askMarket = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(500)
            .build()

        val bidLimit = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(15_000))
            .symbol(Symbol.AAPL)
            .shares(500)
            .build()

        // when
        orderBook.addOrder(askMarket)
        orderBook.addOrder(bidLimit)

        val match = orderBook.match(PriceType.MARKET, OrderType.ASK)
        val bidsPoll = orderBook.bidsPoll()
        val asksPoll = orderBook.asksPoll()

        // then
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(bidsPoll).isNull()
        Assertions.assertThat(asksPoll).isNull()

        Assertions.assertThat(match[0].orderId).isEqualTo(askMarket.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(askMarket.shares)
        Assertions.assertThat(match[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(bidLimit.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(bidLimit.price)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(bidLimit.shares)
    }

    @Test
    fun askMarketBigBidMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bigBidLimit = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(50_000))
            .shares(2000)
            .symbol(Symbol.AAPL)
            .build()

        val smallMarketAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .shares(1200)
            .symbol(Symbol.AAPL)
            .build()

        // when
        orderBook.addOrder(bigBidLimit)
        orderBook.addOrder(smallMarketAsk)

        val match = orderBook.match(PriceType.MARKET, OrderType.ASK)
        val asksPoll = orderBook.asksPoll()
        val bidsPoll = orderBook.bidsPoll()

        // then
        Assertions.assertThat(match).hasSize(1)
        Assertions.assertThat(asksPoll).isNull()
        Assertions.assertThat(bidsPoll).isNotNull
        Assertions.assertThat(match[0].matchedEntries).hasSize(1)

        Assertions.assertThat(match[0].orderId).isEqualTo(smallMarketAsk.orderId)
        Assertions.assertThat(match[0].shares).isEqualTo(smallMarketAsk.shares)
        Assertions.assertThat(match[0].matchedEntries[0].orderId).isEqualTo(bigBidLimit.orderId)
        Assertions.assertThat(match[0].matchedEntries[0].shares).isEqualTo(smallMarketAsk.shares)
        Assertions.assertThat(match[0].matchedEntries[0].price).isEqualTo(bigBidLimit.price)

        Assertions.assertThat(bidsPoll!!.orderId).isEqualTo(bigBidLimit.orderId)
        Assertions.assertThat(bidsPoll.shares()).isEqualTo(bigBidLimit.shares - smallMarketAsk.shares)
    }

    @Test
    fun askMarketBigAskMatch() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)

        val bigMarketAsk = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.ASK)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.ZERO)
            .symbol(Symbol.AAPL)
            .shares(3000)
            .build()

        val smallLimitBid1 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .symbol(Symbol.AAPL)
            .shares(1000)
            .build()

        val smallLimitBid2 = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.BID)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal(10_000))
            .symbol(Symbol.AAPL)
            .shares(1500)
            .build()

        // when
        orderBook.addOrder(smallLimitBid1)
        val match1 = orderBook.match(smallLimitBid1.priceType, smallLimitBid1.orderType)
        orderBook.addOrder(smallLimitBid2)
        val match2 = orderBook.match(smallLimitBid2.priceType, smallLimitBid2.orderType)
        orderBook.addOrder(bigMarketAsk)
        val match3 = orderBook.match(bigMarketAsk.priceType, bigMarketAsk.orderType)
        val asksPoll = orderBook.asksPoll()
        val bidsPoll = orderBook.bidsPoll()

        log.info("$match3")
        log.info("${match3[0]}")
        log.info("${match3[1]}")

        // then
        Assertions.assertThat(match1).hasSize(0)
        Assertions.assertThat(match2).hasSize(0)
        Assertions.assertThat(match3).hasSize(2)
        Assertions.assertThat(asksPoll).isNotNull
        Assertions.assertThat(bidsPoll).isNull()

        Assertions.assertThat(match3[0].orderId).isEqualTo(smallLimitBid1.orderId)
        Assertions.assertThat(match3[0].shares).isEqualTo(smallLimitBid1.shares)
        Assertions.assertThat(match3[0].price).isEqualTo(smallLimitBid1.price)

        Assertions.assertThat(match3[1].orderId).isEqualTo(smallLimitBid2.orderId)
        Assertions.assertThat(match3[1].shares).isEqualTo(smallLimitBid2.shares)
        Assertions.assertThat(match3[1].price).isEqualTo(smallLimitBid2.price)

        Assertions.assertThat(asksPoll!!.orderId).isEqualTo(bigMarketAsk.orderId)
        Assertions.assertThat(asksPoll.shares()).isEqualTo(500)
    }

    @Test
    fun realSimulation() {
        // given
        val orderBook = OrderBook(Symbol.AAPL)
        val ask1 = Order.Builder()
            .orderId("03fc44bf-b268-49a8-9331-a10f4c8ec5f7")
            .orderType(OrderType.ASK)
            .symbol(Symbol.AAPL)
            .shares(469)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(99334))
            .build()
        val bid1 = Order.Builder()
            .orderId("50fb7629-e272-4242-b8e6-415b637f63e4")
            .orderType(OrderType.BID)
            .symbol(Symbol.AAPL)
            .shares(438)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(98572))
            .build()
        val ask2 = Order.Builder()
            .orderId("0e044b36-f6ec-4179-8d96-f951db4e8745")
            .orderType(OrderType.ASK)
            .symbol(Symbol.AAPL)
            .shares(2608)
            .priceType(PriceType.LIMIT)
            .price(BigDecimal.valueOf(96906))
            .build()
        val bid2 = Order.Builder()
            .orderId("dec9c848-0e3f-41e6-9070-63caddcab325")
            .orderType(OrderType.BID)
            .symbol(Symbol.AAPL)
            .shares(1944)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.valueOf(97408))
            .build()
        val bid3 = Order.Builder()
            .orderId("ea35a7d3-e48a-4993-8dbf-969394f14fae")
            .orderType(OrderType.BID)
            .symbol(Symbol.AAPL)
            .shares(683)
            .priceType(PriceType.MARKET)
            .price(BigDecimal.valueOf(94061))
            .build()

        // when
        orderBook.addOrder(ask1)
        val match1 = orderBook.match(ask1.priceType, ask1.orderType) // null
        orderBook.addOrder(bid1)
        val match2 = orderBook.match(bid1.priceType, bid1.orderType) // null
        orderBook.addOrder(ask2)
        val match3 = orderBook.match(ask2.priceType, ask2.orderType) // not null
        orderBook.addOrder(bid2)
        val match4 = orderBook.match(bid2.priceType, bid2.orderType) // not null
        orderBook.addOrder(bid3)
        val match5 = orderBook.match(bid3.priceType, bid3.orderType)
        val leftAsk = orderBook.asksPoll()


        // then
        log.info("{}", match1)
        log.info("{}", match2)
        log.info("{}", match3)
        log.info("{}", match4)
        log.info("{}", match5)
        log.info("{}", leftAsk)
        Assertions.assertThat(match1).hasSize(0)
        Assertions.assertThat(match2).hasSize(0)
        Assertions.assertThat(match3).hasSize(1)
        Assertions.assertThat(match4).hasSize(1)
        Assertions.assertThat(match5).hasSize(1)
        Assertions.assertThat(match3[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match4[0].matchedEntries).hasSize(1)
        Assertions.assertThat(match5[0].matchedEntries).hasSize(2)
        Assertions.assertThat(match3[0].matchedEntries[0].orderId).isEqualTo(ask2.orderId)
        Assertions.assertThat(match4[0].matchedEntries[0].orderId).isEqualTo(ask2.orderId)
        Assertions.assertThat(match5[0].matchedEntries[0].orderId).isEqualTo(ask2.orderId)
        Assertions.assertThat(match5[0].matchedEntries[1].orderId).isEqualTo(ask1.orderId)
        Assertions.assertThat(leftAsk!!.orderId).isEqualTo(ask1.orderId)
        Assertions.assertThat(leftAsk.shares()).isEqualTo(12)
    }
}