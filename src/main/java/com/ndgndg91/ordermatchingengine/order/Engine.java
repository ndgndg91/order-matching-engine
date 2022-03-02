package com.ndgndg91.ordermatchingengine.order;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class Engine {
    private final OrderBooks orderBooks;
    private ApplicationEventPublisher publisher;

    public void addOrder(Order order) {
        orderBooks.addOrder(order);
    }

    public void modifyOrder() {

    }

    public void cancelOrder() {

    }

    public void match() {

    }

}
