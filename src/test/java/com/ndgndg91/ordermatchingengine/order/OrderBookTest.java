package com.ndgndg91.ordermatchingengine.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

class OrderBookTest {
    private static final Logger log = LoggerFactory.getLogger(OrderBookTest.class);

    @Test
    void bids() {
        OrderBook orderBook = new OrderBook();
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

        Assertions.assertThat(orderBook.bidsEmpty()).isFalse();

        OrderEntry e1 = orderBook.bidsPoll();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o4.getOrderId());
        Assertions.assertThat(e1.getShares()).isEqualTo(o4.getShares());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o4.getPriceType());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o4.getPrice());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o4.getTimestamp());

        OrderEntry e2 = orderBook.bidsPoll();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e2.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o3.getTimestamp());

        OrderEntry e3 = orderBook.bidsPoll();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e3.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o1.getTimestamp());

        OrderEntry e4 = orderBook.bidsPoll();
        Assertions.assertThat(e4.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e4.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e4.getPriceType()).isEqualTo(o2.getPriceType());
        Assertions.assertThat(e4.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e4.getTimestamp()).isEqualTo(o2.getTimestamp());

        OrderEntry e5 = orderBook.bidsPoll();
        Assertions.assertThat(e5.getOrderId()).isEqualTo(o5.getOrderId());
        Assertions.assertThat(e5.getShares()).isEqualTo(o5.getShares());
        Assertions.assertThat(e5.getPriceType()).isEqualTo(o5.getPriceType());
        Assertions.assertThat(e5.getPrice()).isEqualTo(o5.getPrice());
        Assertions.assertThat(e5.getTimestamp()).isEqualTo(o5.getTimestamp());
    }

    @Test
    void asks() {
        OrderBook orderBook = new OrderBook();

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

        Assertions.assertThat(orderBook.asksEmpty()).isFalse();

        OrderEntry e1 = orderBook.asksPoll();
        Assertions.assertThat(e1.getOrderId()).isEqualTo(o4.getOrderId());
        Assertions.assertThat(e1.getShares()).isEqualTo(o4.getShares());
        Assertions.assertThat(e1.getPriceType()).isEqualTo(o4.getPriceType());
        Assertions.assertThat(e1.getPrice()).isEqualTo(o4.getPrice());
        Assertions.assertThat(e1.getTimestamp()).isEqualTo(o4.getTimestamp());

        OrderEntry e2 = orderBook.asksPoll();
        Assertions.assertThat(e2.getOrderId()).isEqualTo(o2.getOrderId());
        Assertions.assertThat(e2.getShares()).isEqualTo(o2.getShares());
        Assertions.assertThat(e2.getPriceType()).isEqualTo(o2.getPriceType());
        Assertions.assertThat(e2.getPrice()).isEqualTo(o2.getPrice());
        Assertions.assertThat(e2.getTimestamp()).isEqualTo(o2.getTimestamp());

        OrderEntry e3 = orderBook.asksPoll();
        Assertions.assertThat(e3.getOrderId()).isEqualTo(o5.getOrderId());
        Assertions.assertThat(e3.getShares()).isEqualTo(o5.getShares());
        Assertions.assertThat(e3.getPriceType()).isEqualTo(o5.getPriceType());
        Assertions.assertThat(e3.getPrice()).isEqualTo(o5.getPrice());
        Assertions.assertThat(e3.getTimestamp()).isEqualTo(o5.getTimestamp());

        OrderEntry e4 = orderBook.asksPoll();
        Assertions.assertThat(e4.getOrderId()).isEqualTo(o1.getOrderId());
        Assertions.assertThat(e4.getShares()).isEqualTo(o1.getShares());
        Assertions.assertThat(e4.getPriceType()).isEqualTo(o1.getPriceType());
        Assertions.assertThat(e4.getPrice()).isEqualTo(o1.getPrice());
        Assertions.assertThat(e4.getTimestamp()).isEqualTo(o1.getTimestamp());

        OrderEntry e5 = orderBook.asksPoll();
        Assertions.assertThat(e5.getOrderId()).isEqualTo(o3.getOrderId());
        Assertions.assertThat(e5.getShares()).isEqualTo(o3.getShares());
        Assertions.assertThat(e5.getPriceType()).isEqualTo(o3.getPriceType());
        Assertions.assertThat(e5.getPrice()).isEqualTo(o3.getPrice());
        Assertions.assertThat(e5.getTimestamp()).isEqualTo(o3.getTimestamp());

    }
}