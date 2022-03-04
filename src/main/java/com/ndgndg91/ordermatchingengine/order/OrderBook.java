package com.ndgndg91.ordermatchingengine.order;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
public class OrderBook {
    private final Queue<OrderEntry> bids;
    private final Queue<OrderEntry> asks;

    public OrderBook() {
        this.bids = new PriorityBlockingQueue<>(100, (orderEntry, t1) -> {
            if (orderEntry.getPriceType() == PriceType.MARKET) {
                return -1;
            }

            return t1.getPrice().compareTo(orderEntry.getPrice());
        });
        this.asks = new PriorityBlockingQueue<>(100, (orderEntry, t1) -> {
            if (orderEntry.getPriceType() == PriceType.MARKET) {
                return -1;
            }


            int c = orderEntry.getPrice().compareTo(t1.getPrice());
            if (c == 0) {
                return orderEntry.getTimestamp().compareTo(t1.getTimestamp());
            }

            return c;
        });
    }

    public void addOrder(Order order) {
        OrderEntry e = new OrderEntry(order);
        switch (order.getOrderType()) {
            case ASK:
                asks.add(e);
                break;
            case BID:
                bids.add(e);
                break;
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when adding order, order type is abnormal.");
        }
    }

    public void modifyOrder(Order order) {
        OrderEntry orderEntry;
        switch (order.getOrderType()) {
            case ASK:
                orderEntry = asks.stream()
                        .filter(e -> e.getOrderId().equals(order.getOrderId()))
                        .findFirst().
                        orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when modifying order, not found order " + order.getOrderId()));
                asks.remove(orderEntry);
                asks.add(new OrderEntry(order));
                break;
            case BID:
                orderEntry = bids.stream()
                        .filter(e -> e.getOrderId().equals(order.getOrderId()))
                        .findFirst().
                        orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when modifying order, not found order " + order.getOrderId()));
                bids.remove(orderEntry);
                bids.add(new OrderEntry(order));
                break;
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when modifying order, order type is abnormal.");
        }

    }

    public void cancelOrder(Order order) {
        switch (order.getOrderType()) {
            case ASK:
                asks.remove(new OrderEntry(order));
                break;
            case BID:
                bids.remove(new OrderEntry(order));
                break;
            default:
                throw new IllegalStateException("Order Type is abnormal.");
        }
    }

    public OrderEntry bidsPoll() {
        return this.bids.poll();
    }

    public boolean bidsEmpty() {
        return this.bids.isEmpty();
    }

    public OrderEntry asksPoll() {
        return this.asks.poll();
    }

    public boolean asksEmpty() {
        return this.asks.isEmpty();
    }

    public List<OrderEntry> bidsToList() {
        return new ArrayList<>(this.bids);
    }

    public List<OrderEntry> asksToList() {
        return new ArrayList<>(this.asks);
    }
}
