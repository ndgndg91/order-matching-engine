package com.ndgndg91.ordermatchingenginekotlin.order.service

import com.ndgndg91.ordermatchingenginekotlin.order.*
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.math.BigDecimal
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

    }

    @Test
    fun cancelBids() {

    }

    @Test
    fun emptyMatch() {

    }

    @Test
    fun limitExactMatch() {

    }

    @Test
    fun bigAskLimitMatch() {

    }

    @Test
    fun bigBidLimitMatch() {

    }

    @Test
    fun limitOrderSimulation_1() {

    }

    @Test
    fun limitOrderSimulation_2() {

    }

    @Test
    fun bidMarketEmptyMatch() {

    }

    @Test
    fun bidMarketExactMatch() {

    }

    @Test
    fun bidMarketBigBidMatch() {

    }

    @Test
    fun bidMarketBigAskMatch() {

    }

    @Test
    fun askMarketEmptyMatch() {

    }

    @Test
    fun askMarketExactMatch() {

    }

    @Test
    fun askMarketBigBidMatch() {

    }

    @Test
    fun askMarketBigAskMatch() {

    }
}