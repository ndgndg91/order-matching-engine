package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import com.ndgndg91.ordermatchingengine.order.MatchResult;
import com.ndgndg91.ordermatchingengine.order.Order;
import com.ndgndg91.ordermatchingengine.order.OrderBook;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
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
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "Not Found Symbol " + order.getSymbol());
        orderBook.addOrder(order);
    }

    public void modifyOrder(Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "Not Found Symbol " + order.getSymbol());
        orderBook.modifyOrder(order);
    }

    public void cancelOrder(Order order) {
        OrderBook orderBook = orderBooks.get(order.getSymbol());
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "Not Found Symbol " + order.getSymbol());
        orderBook.cancelOrder(order);
    }

    public MatchResult match(Symbol symbol) {
        OrderBook orderBook = orderBooks.get(symbol);
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "Not Found Symbol " + symbol);
        return null;
    }

    public Optional<OrderBook> findOrderBooksBySymbol(Symbol symbol) {
        return Optional.ofNullable(orderBooks.get(symbol));
    }
}
