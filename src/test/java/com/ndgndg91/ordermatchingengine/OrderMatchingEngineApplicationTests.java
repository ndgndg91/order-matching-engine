package com.ndgndg91.ordermatchingengine;

import com.ndgndg91.ordermatchingengine.order.*;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;

class OrderMatchingEngineApplicationTests {

    @Test
    void contextLoads() {
        SortedSet<OrderEntry> orderEntries = new ConcurrentSkipListSet<>(new ConcurrentSkipListSet<>((orderEntry, t1) -> {
            if (orderEntry.getPriceType() == PriceType.MARKET) {
                return -1;
            } else {
                return orderEntry.getPrice().compareTo(t1.getPrice());
            }
        }));

        Order o1 = Order.builder()
                .orderId(1L)
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(10)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(5000))
                .build();

        Order o2 = Order.builder()
                .orderId(2L)
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(20)
                .priceType(PriceType.LIMIT)
                .price(BigDecimal.valueOf(5500))
                .build();

        OrderEntry e = new OrderEntry(o1);
        OrderEntry e2 = new OrderEntry(o2);
        orderEntries.add(e);
        orderEntries.add(e2);

        Assertions.assertThat(orderEntries).hasSize(2);

        Order anotherOrder = Order.builder()
                .orderId(1L)
                .price(BigDecimal.valueOf(5000))
                .build();

        OrderEntry e3 = new OrderEntry(anotherOrder);
        orderEntries.remove(e3);

        Assertions.assertThat(orderEntries).hasSize(1);


        Order m1 = Order.builder()
                .orderId(1L)
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(100)
                .priceType(PriceType.MARKET)
                .build();

        Order m2 = Order.builder()
                .orderId(3L)
                .orderType(OrderType.ASK)
                .symbol(Symbol.AAPL)
                .shares(50)
                .priceType(PriceType.MARKET)
                .build();

        OrderEntry e4 = new OrderEntry(m1);
        OrderEntry e5 = new OrderEntry(m2);

        orderEntries.add(e4);
        orderEntries.add(e5);

        for (var entry : orderEntries) {
            System.out.println(entry);
        }
    }

}
