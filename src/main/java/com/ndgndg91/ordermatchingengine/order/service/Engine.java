package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import com.ndgndg91.ordermatchingengine.order.*;
import com.ndgndg91.ordermatchingengine.order.dto.request.AddOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.request.CancelOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.request.ModifyOrderRequest;
import com.ndgndg91.ordermatchingengine.order.event.OrderMatchTriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class Engine {
    private final OrderBooks orderBooks;
    private final MatchedOrders matchedOrders;
    private final ApplicationEventPublisher publisher;

    public void addOrder(AddOrderRequest request) {
        final var orderID = UUID.randomUUID().toString();
        final var order = Order.builder()
                .orderId(orderID)
                .orderType(OrderType.valueOf(request.getOrderType()))
                .symbol(Symbol.valueOf(request.getSymbol()))
                .shares(request.getShares())
                .priceType(PriceType.valueOf(request.getPriceType()))
                .price(request.getPrice())
                .timestamp(LocalDateTime.now())
                .build();

        log.info("{}", order);
        orderBooks.addOrder(order);
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol(), order.getPriceType(), order.getOrderType()));
    }

    public void modifyOrder(ModifyOrderRequest request) {
        final var order = Order.builder()
                .orderId(request.getOrderId())
                .orderType(OrderType.valueOf(request.getOrderType()))
                .symbol(Symbol.valueOf(request.getSymbol()))
                .shares(request.getShares())
                .priceType(PriceType.valueOf(request.getPriceType()))
                .price(request.getPrice())
                .timestamp(LocalDateTime.now())
                .build();
        orderBooks.modifyOrder(order);
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol(), order.getPriceType(), order.getOrderType()));
    }

    public void cancelOrder(CancelOrderRequest request) {
        final var order = Order.builder()
                .orderId(request.getOrderId())
                .orderType(OrderType.valueOf(request.getOrderType()))
                .symbol(Symbol.valueOf(request.getSymbol()))
                .build();
        orderBooks.cancelOrder(order);
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol(), order.getPriceType(), order.getOrderType()));
    }

    public Optional<OrderEntry> pollBids(Symbol symbol) {
        OrderBook orderBook = orderBooks.findOrderBooksBySymbol(symbol)
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found order book by : " + symbol));

        return orderBook.bidsPoll();
    }

    public Optional<OrderEntry> pollAsks(Symbol symbol) {
        OrderBook orderBook = orderBooks.findOrderBooksBySymbol(symbol)
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found order book by : " + symbol));
        return orderBook.asksPoll();
    }

    public List<MatchResult> findAllMatchResults(Symbol symbol) {
        return matchedOrders.findAll(symbol);
    }

    @Async
    @EventListener
    public void match(OrderMatchTriggerEvent event) {
        log.info("{}", event);
        orderBooks.match(event.getSymbol(), event.getPriceType(), event.getOrderType())
                .ifPresent(matchedOrders::addMatchedOrder);
    }
}
