package com.ndgndg91.ordermatchingengine.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.UUID;

class OrderBookTest {
    private static final Logger log = LoggerFactory.getLogger(OrderBookTest.class);

    @Test
    void addBids() {
        OrderBook orderBook = new OrderBook(Symbol.AAPL);
        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(5000))
                .priceType(PriceType.LIMIT)
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(10000))
                .priceType(PriceType.LIMIT)
                .shares(150)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o4 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.ZERO)
                .priceType(PriceType.MARKET)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o5 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(50)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);
        orderBook.addOrder(o4);
        orderBook.addOrder(o5);

        OrderEntry e1 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o4.getOrderId());
        Assertions.assertThat(e1.getShares()).isEqualTo(o4.getShares());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o4.getPriceType());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o4.getPrice());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o4.getTimestamp());

        OrderEntry e2 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e2.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o3.getTimestamp());

        OrderEntry e3 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e3.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o1.getTimestamp());

        OrderEntry e4 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e4.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e4.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e4.getPriceType()).isEqualTo(o2.getPriceType());
        Assertions.assertThat(e4.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e4.getTimestamp()).isEqualTo(o2.getTimestamp());

        OrderEntry e5 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e5.getOrderId()).isEqualTo(o5.getOrderId());
        Assertions.assertThat(e5.getShares()).isEqualTo(o5.getShares());
        Assertions.assertThat(e5.getPriceType()).isEqualTo(o5.getPriceType());
        Assertions.assertThat(e5.getPrice()).isEqualTo(o5.getPrice());
        Assertions.assertThat(e5.getTimestamp()).isEqualTo(o5.getTimestamp());

        Optional<OrderEntry> emptyOE = orderBook.bidsPoll();
        Assertions.assertThatThrownBy(emptyOE::orElseThrow)
                .isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void modifyBids() {
        //given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);
        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(5000))
                .priceType(PriceType.LIMIT)
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        String o2Id = UUID.randomUUID().toString();
        Order o2 = Order.builder()
                .orderId(o2Id)
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(10000))
                .priceType(PriceType.LIMIT)
                .shares(150)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);


        //when
        Order mo = Order.builder()
                .orderId(o2Id)
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(6000))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.modifyOrder(mo);

        //then
        OrderEntry e1 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e1.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o3.getTimestamp());

        OrderEntry e2 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(mo.getOrderId());
        Assertions.assertThat(e2.getPrice()).isEqualTo(mo.getPrice());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(mo.getPriceType());
        Assertions.assertThat(e2.getShares()).isEqualTo(mo.getShares());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(mo.getTimestamp());

        OrderEntry e3 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e3.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o1.getTimestamp());
    }

    @Test
    void cancelBids() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(100)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(15000))
                .build();

        Order o2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(200)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(13000))
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(150)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(12000))
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);

        // when
        orderBook.cancelOrder(o1);

        // then
        OrderEntry e1 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o2.getTimestamp());
        Assertions.assertThat(e1.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o2.getPriceType());

        OrderEntry e2 = orderBook.bidsPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o3.getTimestamp());
        Assertions.assertThat(e2.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o3.getPriceType());

        Optional<OrderEntry> emptyE1 = orderBook.asksPoll();
        Optional<OrderEntry> emptyE2 = orderBook.bidsPoll();
        Assertions.assertThatThrownBy(emptyE1::orElseThrow).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(emptyE2::orElseThrow).isInstanceOf(NoSuchElementException.class);
    }

    @Test
    void addAsks() {
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(5000))
                .priceType(PriceType.LIMIT)
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(10000))
                .priceType(PriceType.LIMIT)
                .shares(150)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o4 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.ZERO)
                .priceType(PriceType.MARKET)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o5 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(50)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);
        orderBook.addOrder(o4);
        orderBook.addOrder(o5);

        OrderEntry e1 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o4.getOrderId());
        Assertions.assertThat(e1.getShares()).isEqualTo(o4.getShares());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o4.getPriceType());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o4.getPrice());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o4.getTimestamp());

        OrderEntry e2 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e2.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o2.getPriceType());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o2.getTimestamp());

        OrderEntry e3 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o5.getOrderId());
        Assertions.assertThat(e3.getShares()).isEqualTo(o5.getShares());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o5.getPriceType());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o5.getPrice());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o5.getTimestamp());

        OrderEntry e4 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e4.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e4.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e4.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e4.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e4.getTimestamp()).isEqualTo(o1.getTimestamp());

        OrderEntry e5 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e5.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e5.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e5.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e5.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e5.getTimestamp()).isEqualTo(o3.getTimestamp());
    }

    @Test
    void modifyAsks() {
        //given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);
        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(5000))
                .priceType(PriceType.LIMIT)
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        String o2Id = UUID.randomUUID().toString();
        Order o2 = Order.builder()
                .orderId(o2Id)
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(4500))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(10000))
                .priceType(PriceType.LIMIT)
                .shares(150)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);


        //when
        Order mo = Order.builder()
                .orderId(o2Id)
                .orderType(OrderType.ASK)
                .price(BigDecimal.valueOf(6000))
                .priceType(PriceType.LIMIT)
                .shares(200)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.modifyOrder(mo);

        //then
        OrderEntry e1 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e1.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o1.getTimestamp());

        OrderEntry e2 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(mo.getOrderId());
        Assertions.assertThat(e2.getPrice()).isEqualTo(mo.getPrice());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(mo.getPriceType());
        Assertions.assertThat(e2.getShares()).isEqualTo(mo.getShares());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(mo.getTimestamp());

        OrderEntry e3 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e3.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o3.getTimestamp());
    }

    @Test
    void cancelAsks() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order o1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(100)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(15000))
                .build();

        Order o2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(200)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(13000))
                .build();

        Order o3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .shares(150)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(12000))
                .build();

        orderBook.addOrder(o1);
        orderBook.addOrder(o2);
        orderBook.addOrder(o3);

        // when
        orderBook.cancelOrder(o1);

        // then
        OrderEntry e1 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o3.getTimestamp());
        Assertions.assertThat(e1.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o3.getPriceType());

        OrderEntry e2 = orderBook.asksPoll().orElseThrow();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o2.getTimestamp());
        Assertions.assertThat(e2.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o2.getPriceType());

        Optional<OrderEntry> emptyE1 = orderBook.asksPoll();
        Optional<OrderEntry> emptyE2 = orderBook.bidsPoll();
        Assertions.assertThatThrownBy(emptyE1::orElseThrow).isInstanceOf(NoSuchElementException.class);
        Assertions.assertThatThrownBy(emptyE2::orElseThrow).isInstanceOf(NoSuchElementException.class);
    }
}