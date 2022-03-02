package com.ndgndg91.ordermatchingengine.order;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderBooks {
    private final Map<Symbol, OrderBook> orderBooks;

    public OrderBooks() {
        this.orderBooks = new ConcurrentHashMap<>();
        for (var s : Symbol.values()) {
            this.orderBooks.put(s, new OrderBook());
        }
    }

    public void addOrder(Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook == null) throw new IllegalArgumentException("Not Found Symbol" + order.getSymbol());
        orderBook.addOrder(order);
    }

    public void cancelOrder(Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook == null) throw new IllegalArgumentException("Not Found Symbol" + order.getSymbol());
        orderBook.cancelOrder(order);
    }
}
