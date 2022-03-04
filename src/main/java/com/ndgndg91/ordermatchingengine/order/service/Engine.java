package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.order.*;
import com.ndgndg91.ordermatchingengine.order.dto.request.AddOrderRequest;
import com.ndgndg91.ordermatchingengine.order.event.OrderMatchTriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class Engine {
    private final OrderBooks orderBooks;
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
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol()));
    }

    public void modifyOrder(Order order) {
        orderBooks.modifyOrder(order);
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol()));
    }

    public void cancelOrder(Order order) {
        orderBooks.cancelOrder(order);
        publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol()));
    }

    @Async
    @EventListener
    public void match(OrderMatchTriggerEvent event) {
        log.info("{}", event);
        orderBooks.match(event.getSymbol());
    }

    public Optional<OrderBook> findOrderBookBySymbol(final Symbol symbol) {
        return orderBooks.findOrderBooksBySymbol(symbol);
    }

}
