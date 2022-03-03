package com.ndgndg91.ordermatchingengine.order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.stream.Collectors;

public class OrderBook {
    private final SortedSet<OrderEntry> bids;
    private final SortedSet<OrderEntry> asks;

    public OrderBook() {
        this.bids = new ConcurrentSkipListSet<>((orderEntry, t1) -> {
            if (orderEntry.getPriceType() == PriceType.MARKET) {
                return -1;
            } else {
                return orderEntry.getPrice().compareTo(t1.getPrice());
            }
        });
        this.asks = new ConcurrentSkipListSet<>((orderEntry, t1) -> {
            if (t1.getPriceType() == PriceType.MARKET) {
                return -1;
            } else {
                return t1.getPrice().compareTo(orderEntry.getPrice());
            }
        });
    }

    public void addOrder(Order order) {
        switch (order.getOrderType()) {
            case ASK:
                asks.add(new OrderEntry(order));
                break;
            case BID:
                bids.add(new OrderEntry(order));
                break;
            default:
                throw new IllegalStateException("Order Type is abnormal.");
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

    public List<OrderEntry> bidsToList() {
        return new ArrayList<>(this.bids);
    }

    public List<OrderEntry> asksToList() {
        return new ArrayList<>(this.asks);
    }
}
