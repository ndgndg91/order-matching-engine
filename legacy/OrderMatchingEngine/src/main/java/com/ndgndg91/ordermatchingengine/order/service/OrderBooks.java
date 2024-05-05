package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import com.ndgndg91.ordermatchingengine.order.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OrderBooks {
    private static final String MESSAGE = "Not Found Symbol %s";
    private final Map<Symbol, OrderBook> m;

    public OrderBooks() {
        this.m = new ConcurrentHashMap<>();
        for (var s : Symbol.values()) {
            this.m.put(s, new OrderBook(s));
        }
    }

    public void addOrder(Order order) {
        OrderBook orderBook = m.get(order.getSymbol());
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, order.getSymbol()));
        orderBook.addOrder(order);
    }

    public void modifyOrder(Order order) {
        OrderBook orderBook = m.get(order.getSymbol());
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, order.getSymbol()));
        orderBook.modifyOrder(order);
    }

    public void cancelOrder(Order order) {
        OrderBook orderBook = m.get(order.getSymbol());
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, order.getSymbol()));
        orderBook.cancelOrder(order);
    }

    public Optional<MatchResult> match(Symbol symbol, PriceType priceType, OrderType orderType) {
        OrderBook orderBook = m.get(symbol);
        if (orderBook == null)
            throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), String.format(MESSAGE, symbol));
        return Optional.ofNullable(orderBook.match(priceType, orderType));
    }

    public Optional<OrderBook> findOrderBooksBySymbol(Symbol symbol) {
        return Optional.ofNullable(m.get(symbol));
    }
}
