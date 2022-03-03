package com.ndgndg91.ordermatchingengine.order.service;

import com.ndgndg91.ordermatchingengine.order.Order;
import com.ndgndg91.ordermatchingengine.order.OrderBook;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import com.ndgndg91.ordermatchingengine.order.event.OrderMatchTriggerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class Engine {
    private final OrderBooks orderBooks;
    private final ApplicationEventPublisher publisher;

    public void addOrder(Order order) {
        switch (order.getPriceType()) {
            case MARKET:
                match(order);
                break;
            case LIMIT:
                orderBooks.addOrder(order);
                publisher.publishEvent(new OrderMatchTriggerEvent(order.getSymbol()));
                break;
            default:
                throw new IllegalArgumentException("price type is abnormal.");
        }
    }

    public void modifyOrder(Order order) {
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

    private void match(Order order) {
        orderBooks.match(order);
    }


    public Optional<OrderBook> findOrderBookBySymbol(final Symbol symbol) {
        return orderBooks.findOrderBooksBySymbol(symbol);
    }

}
