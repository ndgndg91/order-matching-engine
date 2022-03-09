package com.ndgndg91.ordermatchingengine.order;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
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

    @Test
    void emptyMatch() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);
        // when
        MatchResult match = orderBook.match(PriceType.LIMIT, OrderType.ASK);
        // then
        Assertions.assertThat(match).isNull();
    }

    @Test
    void limitExactMatch() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order appleAsk100 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order appleBid100 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(100)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        orderBook.addOrder(appleAsk100);
        orderBook.addOrder(appleBid100);

        // when
        MatchResult match = orderBook.match(PriceType.LIMIT, OrderType.BID);
        log.info("{}", match);
        List<MatchedEntry> matchedEntries = match.getMatchedEntries();

        // then
        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(match.getOrderId()).isEqualTo(appleBid100.getOrderId());
        Assertions.assertThat(match.getSymbol()).isEqualTo(appleBid100.getSymbol());
        Assertions.assertThat(match.getShares()).isEqualTo(appleBid100.getShares());
        Assertions.assertThat(match.getShares()).isEqualTo(appleAsk100.getShares());
        Assertions.assertThat(match.getPrice()).isEqualTo(appleAsk100.getPrice());
        Assertions.assertThat(match.getPrice()).isEqualTo(appleBid100.getPrice());
        Assertions.assertThat(match.getPriceType()).isEqualTo(appleAsk100.getPriceType());
        Assertions.assertThat(match.getPriceType()).isEqualTo(appleBid100.getPriceType());
        Assertions.assertThat(match.getTimestamp()).isEqualTo(appleBid100.getTimestamp());
        Assertions.assertThat(matchedEntries).hasSize(1);
        Assertions.assertThat(matchedEntries.get(0).getOrderId()).isEqualTo(appleAsk100.getOrderId());
        Assertions.assertThat(matchedEntries.get(0).getOrderType()).isEqualTo(appleAsk100.getOrderType());
        Assertions.assertThat(matchedEntries.get(0).getPriceType()).isEqualTo(appleAsk100.getPriceType());
        Assertions.assertThat(matchedEntries.get(0).getPrice()).isEqualTo(appleAsk100.getPrice());
        Assertions.assertThat(matchedEntries.get(0).getShares()).isEqualTo(appleAsk100.getShares());
        Assertions.assertThat(matchedEntries.get(0).getTimestamp()).isEqualTo(appleAsk100.getTimestamp());
    }

    @Test
    void bigAskLimitMatch() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.GOOG);

        Order bigAsk = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(1500)
                .symbol(Symbol.GOOG)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallBid1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(200)
                .symbol(Symbol.GOOG)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallBid2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(500)
                .symbol(Symbol.GOOG)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallBid3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(800)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(bigAsk);
        orderBook.addOrder(smallBid1);
        orderBook.addOrder(smallBid2);
        orderBook.addOrder(smallBid3);

        // then
        MatchResult match1 = orderBook.match(PriceType.LIMIT, OrderType.ASK);
        List<MatchedEntry> matchedEntries1 = match1.getMatchedEntries();
        MatchResult match2 = orderBook.match(PriceType.LIMIT, OrderType.ASK);
        List<MatchedEntry> matchedEntries2 = match2.getMatchedEntries();
        MatchResult match3 = orderBook.match(PriceType.LIMIT, OrderType.ASK);
        List<MatchedEntry> matchedEntries3 = match3.getMatchedEntries();
        MatchResult match4 = orderBook.match(PriceType.LIMIT, OrderType.ASK);

        log.info("{}", match1);
        log.info("{}", match2);
        log.info("{}", match3);

        Assertions.assertThat(match1).isNotNull();
        Assertions.assertThat(match2).isNotNull();
        Assertions.assertThat(match3).isNotNull();
        Assertions.assertThat(match4).isNull();

        Assertions.assertThat(match1.getOrderId()).isEqualTo(smallBid1.getOrderId());
        Assertions.assertThat(match2.getOrderId()).isEqualTo(smallBid2.getOrderId());
        Assertions.assertThat(match3.getOrderId()).isEqualTo(smallBid3.getOrderId());

        Assertions.assertThat(match1.getOrderType()).isEqualTo(smallBid1.getOrderType());
        Assertions.assertThat(match2.getOrderType()).isEqualTo(smallBid2.getOrderType());
        Assertions.assertThat(match3.getOrderType()).isEqualTo(smallBid3.getOrderType());

        Assertions.assertThat(match1.getShares()).isEqualTo(smallBid1.getShares());
        Assertions.assertThat(match2.getShares()).isEqualTo(smallBid2.getShares());
        Assertions.assertThat(match3.getShares()).isEqualTo(smallBid3.getShares());

        Assertions.assertThat(matchedEntries1).hasSize(1);
        Assertions.assertThat(matchedEntries2).hasSize(1);
        Assertions.assertThat(matchedEntries3).hasSize(1);

        Assertions.assertThat(matchedEntries1.get(0).getOrderId()).isEqualTo(bigAsk.getOrderId());
        Assertions.assertThat(matchedEntries2.get(0).getOrderId()).isEqualTo(bigAsk.getOrderId());
        Assertions.assertThat(matchedEntries3.get(0).getOrderId()).isEqualTo(bigAsk.getOrderId());
    }

    @Test
    void bigBidLimitMatch() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AMZN);

        Order bigBid = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.AMZN)
                .shares(1500)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallAsk1 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.AMZN)
                .shares(500)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallAsk2 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.AMZN)
                .shares(800)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallAsk3 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.AMZN)
                .shares(200)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(bigBid);
        orderBook.addOrder(smallAsk1);
        orderBook.addOrder(smallAsk2);
        orderBook.addOrder(smallAsk3);

        MatchResult match1 = orderBook.match(PriceType.LIMIT, OrderType.BID);
        log.info("{}", match1);
        List<MatchedEntry> matchedEntries1 = match1.getMatchedEntries();
        MatchResult match2 = orderBook.match(PriceType.LIMIT, OrderType.BID);

        // then
        Assertions.assertThat(match1).isNotNull();
        Assertions.assertThat(match2).isNull();

        Assertions.assertThat(match1.getOrderId()).isEqualTo(bigBid.getOrderId());
        Assertions.assertThat(match1.getShares()).isEqualTo(bigBid.getShares());
        Assertions.assertThat(match1.getPrice()).isEqualTo(bigBid.getPrice());

        Assertions.assertThat(matchedEntries1).hasSize(3);
        Assertions.assertThat(matchedEntries1.get(0).getOrderId()).isEqualTo(smallAsk1.getOrderId());
        Assertions.assertThat(matchedEntries1.get(1).getOrderId()).isEqualTo(smallAsk2.getOrderId());
        Assertions.assertThat(matchedEntries1.get(2).getOrderId()).isEqualTo(smallAsk3.getOrderId());
    }

    @Test
    void limitOrderSimulation1() {
        // given
        // bigAsk -> smallBid -> bigBid -> bigAsk
        OrderBook orderBook = new OrderBook(Symbol.TSLA);

        Order bigAsk = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.TSLA)
                .shares(1500)
                .timestamp(LocalDateTime.now())
                .build();

        Order smallBid = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.TSLA)
                .shares(500)
                .timestamp(LocalDateTime.now())
                .build();

        Order bigBid = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.TSLA)
                .shares(2000)
                .timestamp(LocalDateTime.now())
                .build();

        Order lastBigAsk = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .symbol(Symbol.TSLA)
                .shares(1500)
                .timestamp(LocalDateTime.now())
                .build();

        log.info("{}", bigAsk);
        log.info("{}", smallBid);
        log.info("{}", bigBid);
        log.info("{}", lastBigAsk);

        // when
        orderBook.addOrder(bigAsk);
        MatchResult match1 = orderBook.match(PriceType.LIMIT, OrderType.BID);

        orderBook.addOrder(smallBid);
        MatchResult match2 = orderBook.match(PriceType.LIMIT, OrderType.BID);
        List<MatchedEntry> matchedEntries2 = match2.getMatchedEntries();

        orderBook.addOrder(bigBid);
        MatchResult match3 = orderBook.match(PriceType.LIMIT, OrderType.BID);

        orderBook.addOrder(lastBigAsk);
        MatchResult match4 = orderBook.match(PriceType.LIMIT, OrderType.BID);
        List<MatchedEntry> matchedEntries4 = match4.getMatchedEntries();

        MatchResult match5 = orderBook.match(PriceType.LIMIT, OrderType.BID);


        log.info("{}", match1);
        log.info("{}", match2);
        log.info("{}", match3);
        log.info("{}", match4);
        log.info("{}", match5);

        // then
        Assertions.assertThat(match1).isNull();
        Assertions.assertThat(match2).isNotNull();
        Assertions.assertThat(match3).isNull();
        Assertions.assertThat(match4).isNotNull();
        Assertions.assertThat(match5).isNull();

        Assertions.assertThat(matchedEntries2).hasSize(1);
        Assertions.assertThat(matchedEntries4).hasSize(2);

        Optional<OrderEntry> emptyBid = orderBook.bidsPoll();
        OrderEntry leftAsk = orderBook.asksPoll().orElseThrow();
        log.info("{}", leftAsk);
        Assertions.assertThatThrownBy(emptyBid::orElseThrow).isInstanceOf(NoSuchElementException.class);

        Assertions.assertThat(leftAsk).isNotNull();
        Assertions.assertThat(leftAsk.shares()).isEqualTo(500);
    }

    @Test
    void limitOrderSimulation2() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.MSFT);

        Order bid9500 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .price(BigDecimal.valueOf(9_500))
                .priceType(PriceType.LIMIT)
                .shares(500)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order bid9600 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_600))
                .shares(500)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order bid9700 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_700))
                .shares(500)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order ask9800 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_800))
                .shares(200)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order ask9900 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_900))
                .shares(300)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order ask10000 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(600)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        Order marketMaker = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(1000)
                .symbol(Symbol.MSFT)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(bid9500); // 500
        orderBook.addOrder(bid9600); // 500
        orderBook.addOrder(bid9700); // 500

        orderBook.addOrder(ask9800); // 200
        orderBook.addOrder(ask9900); // 300
        orderBook.addOrder(ask10000); // 600

        MatchResult match1 = orderBook.match(PriceType.LIMIT, OrderType.BID);
        log.info("{}", match1);
        orderBook.addOrder(marketMaker); // 1000

        MatchResult match2 = orderBook.match(PriceType.LIMIT, OrderType.BID);
        List<MatchedEntry> matchedEntries2 = match2.getMatchedEntries();
        int sum = match2.matchedShare();
        BigDecimal ask9800Price = ask9800.getPrice().multiply(BigDecimal.valueOf(ask9800.getShares()));
        BigDecimal ask9900Price = ask9900.getPrice().multiply(BigDecimal.valueOf(ask9900.getShares()));
        BigDecimal ask10000Price = ask10000.getPrice().multiply(BigDecimal.valueOf(500));
        BigDecimal askSums = ask9800Price.add(ask9900Price).add(ask10000Price);
        int askTotalShares = ask9800.getShares() + ask9900.getShares() + 500;
        BigDecimal askAveragePrice = askSums.divide(BigDecimal.valueOf(askTotalShares), RoundingMode.CEILING);
        log.info("{} {}", askAveragePrice, match2.averagePrice());
        log.info("{}", match2);

        OrderEntry remainAsk = orderBook.asksPoll().orElseThrow();
        OrderEntry remainBid1 = orderBook.bidsPoll().orElseThrow();
        OrderEntry remainBid2 = orderBook.bidsPoll().orElseThrow();
        OrderEntry remainBid3 = orderBook.bidsPoll().orElseThrow();

        // then
        Assertions.assertThat(match1).isNull();
        Assertions.assertThat(match2).isNotNull();
        Assertions.assertThat(matchedEntries2).hasSize(3);

        Assertions.assertThat(remainAsk).isNotNull();
        Assertions.assertThat(remainAsk.shares()).isEqualTo(100);
        Assertions.assertThat(remainBid1).isNotNull();
        Assertions.assertThat(remainBid2).isNotNull();
        Assertions.assertThat(remainBid3).isNotNull();
        Assertions.assertThat(remainBid1.getOrderId()).isEqualTo(bid9700.getOrderId());
        Assertions.assertThat(remainBid2.getOrderId()).isEqualTo(bid9600.getOrderId());
        Assertions.assertThat(remainBid3.getOrderId()).isEqualTo(bid9500.getOrderId());

        Assertions.assertThat(match2.getOrderId()).isEqualTo(marketMaker.getOrderId());
        Assertions.assertThat(match2.getPrice()).isEqualTo(marketMaker.getPrice());
        Assertions.assertThat(match2.getShares()).isEqualTo(marketMaker.getShares());
        Assertions.assertThat(match2.getShares()).isEqualTo(sum);
        Assertions.assertThat(match2.averagePrice()).isEqualTo(askAveragePrice);

        Assertions.assertThat(matchedEntries2.get(0).getOrderId()).isEqualTo(ask9800.getOrderId());
        Assertions.assertThat(matchedEntries2.get(1).getOrderId()).isEqualTo(ask9900.getOrderId());
        Assertions.assertThat(matchedEntries2.get(2).getOrderId()).isEqualTo(ask10000.getOrderId());
    }

    @Test
    void bidMarketOrder() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.FB);

        Order askBid10000 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(10_000))
                .shares(1000)
                .symbol(Symbol.FB)
                .timestamp(LocalDateTime.now())
                .build();

        Order askBid9900 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_900))
                .shares(200)
                .symbol(Symbol.FB)
                .timestamp(LocalDateTime.now())
                .build();

        Order bidMarket = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.MARKET)
                .shares(500)
                .symbol(Symbol.FB)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(askBid10000);
        orderBook.addOrder(askBid9900);
        orderBook.addOrder(bidMarket);

        MatchResult match = orderBook.match(PriceType.MARKET, OrderType.BID);
        List<MatchedEntry> matchedEntries = match.getMatchedEntries();
        BigDecimal avgPrice = askBid9900.getPrice()
                .multiply(BigDecimal.valueOf(200))
                .add(askBid10000.getPrice().multiply(BigDecimal.valueOf(300)))
                .divide(BigDecimal.valueOf(500), RoundingMode.CEILING);
        log.info("{}", match);
        log.info("{}", avgPrice);

        // then
        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(matchedEntries).hasSize(2);

        Assertions.assertThat(match.getOrderId()).isEqualTo(bidMarket.getOrderId());
        Assertions.assertThat(match.averagePrice()).isEqualTo(avgPrice);
        Assertions.assertThat(match.getShares()).isEqualTo(500);
        Assertions.assertThat(match.getPriceType()).isEqualTo(PriceType.MARKET);

        Assertions.assertThat(matchedEntries.get(0).getShares()).isEqualTo(200);
        Assertions.assertThat(matchedEntries.get(1).getShares()).isEqualTo(300);
        Assertions.assertThat(matchedEntries.get(0).getPrice()).isEqualTo(BigDecimal.valueOf(9_900));
        Assertions.assertThat(matchedEntries.get(1).getPrice()).isEqualTo(BigDecimal.valueOf(10_000));
    }

    @Test
    void askMarketOrder() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order limitBid9500 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_500))
                .shares(150)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order limitBid9450 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_450))
                .shares(230)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order limitBid9480 = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.BID)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(9_480))
                .shares(220)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        Order marketAsk = Order.builder()
                .orderId(UUID.randomUUID().toString())
                .orderType(OrderType.ASK)
                .priceType(PriceType.MARKET)
                .shares(550)
                .symbol(Symbol.AAPL)
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(limitBid9500);
        orderBook.addOrder(limitBid9480);
        orderBook.addOrder(limitBid9450);
        orderBook.addOrder(marketAsk);

        MatchResult match = orderBook.match(PriceType.MARKET, OrderType.ASK);
        List<MatchedEntry> matchedEntries = match.getMatchedEntries();
        BigDecimal avgPrice = limitBid9500.getPrice().multiply(BigDecimal.valueOf(150))
                .add(limitBid9480.getPrice().multiply(BigDecimal.valueOf(220)))
                .add(limitBid9450.getPrice().multiply(BigDecimal.valueOf(180)))
                .divide(BigDecimal.valueOf(550), RoundingMode.CEILING);
        log.info("{}", match);

        // then
        Assertions.assertThat(match).isNotNull();
        Assertions.assertThat(matchedEntries).hasSize(3);

        Assertions.assertThat(match.getOrderId()).isEqualTo(marketAsk.getOrderId());
        Assertions.assertThat(match.getShares()).isEqualTo(marketAsk.getShares());
        Assertions.assertThat(match.averagePrice()).isEqualTo(avgPrice);

        Assertions.assertThat(matchedEntries.get(0).getPrice()).isEqualTo(limitBid9500.getPrice());
        Assertions.assertThat(matchedEntries.get(1).getPrice()).isEqualTo(limitBid9480.getPrice());
        Assertions.assertThat(matchedEntries.get(2).getPrice()).isEqualTo(limitBid9450.getPrice());
        Assertions.assertThat(matchedEntries.get(0).getShares()).isEqualTo(150);
        Assertions.assertThat(matchedEntries.get(1).getShares()).isEqualTo(220);
        Assertions.assertThat(matchedEntries.get(2).getShares()).isEqualTo(180);
    }

    @Test
    void realSimulation() {
        // given
        OrderBook orderBook = new OrderBook(Symbol.AAPL);

        Order ask1 = Order.builder()
                .orderId("03fc44bf-b268-49a8-9331-a10f4c8ec5f7")
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(469)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(99334))
                .timestamp(LocalDateTime.now())
                .build();
        Order bid1 = Order.builder()
                .orderId("50fb7629-e272-4242-b8e6-415b637f63e4")
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .shares(438)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(98572))
                .timestamp(LocalDateTime.now())
                .build();

        Order ask2 = Order.builder()
                .orderId("0e044b36-f6ec-4179-8d96-f951db4e8745")
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(2608)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(96906))
                .timestamp(LocalDateTime.now())
                .build();

        Order bid2 = Order.builder()
                .orderId("dec9c848-0e3f-41e6-9070-63caddcab325")
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .shares(1944)
                .priceType(PriceType.MARKET)
                .price(BigDecimal.valueOf(97408))
                .timestamp(LocalDateTime.now())
                .build();

        Order bid3 = Order.builder()
                .orderId("ea35a7d3-e48a-4993-8dbf-969394f14fae")
                .orderType(OrderType.BID)
                .symbol(Symbol.AAPL)
                .shares(683)
                .priceType(PriceType.MARKET)
                .price(BigDecimal.valueOf(94061))
                .timestamp(LocalDateTime.now())
                .build();

        // when
        orderBook.addOrder(ask1);
        MatchResult match1 = orderBook.match(ask1.getPriceType(), ask1.getOrderType());
        orderBook.addOrder(bid1);
        MatchResult match2 = orderBook.match(bid1.getPriceType(), bid1.getOrderType());
        orderBook.addOrder(ask2);
        MatchResult match3 = orderBook.match(ask2.getPriceType(), ask2.getOrderType());
        orderBook.addOrder(bid2);
        MatchResult match4 = orderBook.match(bid2.getPriceType(), bid2.getOrderType());
        orderBook.addOrder(bid3);
        MatchResult match5 = orderBook.match(bid3.getPriceType(), bid3.getOrderType());

        OrderEntry leftAsk = orderBook.asksPoll().orElseThrow();


        // then
        log.info("{}", match1);
        log.info("{}", match2);
        log.info("{}", match3);
        log.info("{}", match4);
        log.info("{}", match5);
        log.info("{}", leftAsk);
        Assertions.assertThat(match1).isNull();
        Assertions.assertThat(match2).isNull();
        Assertions.assertThat(match3).isNotNull();
        Assertions.assertThat(match4).isNotNull();
        Assertions.assertThat(match5).isNotNull();

        Assertions.assertThat(match3.getMatchedEntries()).hasSize(1);
        Assertions.assertThat(match4.getMatchedEntries()).hasSize(1);
        Assertions.assertThat(match5.getMatchedEntries()).hasSize(2);

        Assertions.assertThat(match3.getMatchedEntries().get(0).getOrderId()).isEqualTo(ask2.getOrderId());
        Assertions.assertThat(match4.getMatchedEntries().get(0).getOrderId()).isEqualTo(ask2.getOrderId());
        Assertions.assertThat(match5.getMatchedEntries().get(0).getOrderId()).isEqualTo(ask2.getOrderId());
        Assertions.assertThat(match5.getMatchedEntries().get(1).getOrderId()).isEqualTo(ask1.getOrderId());

        Assertions.assertThat(leftAsk.getOrderId()).isEqualTo(ask1.getOrderId());
        Assertions.assertThat(leftAsk.shares()).isEqualTo(12);
    }
}