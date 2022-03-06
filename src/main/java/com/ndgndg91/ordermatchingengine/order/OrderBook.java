package com.ndgndg91.ordermatchingengine.order;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

@Slf4j
public class OrderBook {
    private final Symbol symbol;
    private final Queue<OrderEntry> limitBids;
    private final Queue<OrderEntry> limitAsks;
    private final Queue<OrderEntry> marketBids;
    private final Queue<OrderEntry> marketAsks;

    public OrderBook(Symbol symbol) {
        this.symbol = symbol;
        this.limitBids = new PriorityBlockingQueue<>(100, (orderEntry, t1) -> {
            int c = t1.getPrice().compareTo(orderEntry.getPrice());
            if (c == 0) return orderEntry.getTimestamp().compareTo(t1.getTimestamp());
            return c;
        });
        this.limitAsks = new PriorityBlockingQueue<>(100, Comparator.comparing(OrderEntry::getPrice));
        this.marketBids = new LinkedBlockingQueue<>();
        this.marketAsks = new LinkedBlockingQueue<>();
    }

    private Queue<OrderEntry> selectQueue(PriceType priceType, OrderType orderType) {
        switch (priceType) {
            case MARKET:
                return marketQueue(orderType);
            case LIMIT:
                return limitQueue(orderType);
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found appropriate queue");
        }
    }

    private Queue<OrderEntry> limitQueue(OrderType orderType) {
        switch (orderType) {
            case BID:
                return this.limitBids;
            case ASK:
                return this.limitAsks;
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found appropriate queue");
        }
    }

    private Queue<OrderEntry> marketQueue(OrderType orderType) {
        switch (orderType) {
            case BID:
                return this.marketBids;
            case ASK:
                return this.marketAsks;
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found appropriate queue");
        }
    }

    public void addOrder(Order order) {
        OrderEntry e = new OrderEntry(order);
        selectQueue(order.getPriceType(), order.getOrderType()).add(e);
    }

    public void modifyOrder(Order order) {
        Queue<OrderEntry> queue = selectQueue(order.getPriceType(), order.getOrderType());
        OrderEntry oe = queue.stream()
                .parallel()
                .filter(e -> e.getOrderId().equals(order.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when modifying order, not found order " + order.getOrderId()));
        queue.remove(oe);
        queue.add(new OrderEntry(order));
    }

    public void cancelOrder(Order order) {
        Queue<OrderEntry> queue = selectQueue(order.getPriceType(), order.getOrderType());

        OrderEntry target = queue.stream()
                .parallel()
                .filter(e -> e.getOrderId().equals(order.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when canceling order, not found order : " + order.getOrderId()));
        queue.remove(target);
    }

    public Optional<OrderEntry> bidsPoll() {
        if (!this.marketBids.isEmpty()) {
            return Optional.ofNullable(this.marketBids.poll());
        }

        return Optional.ofNullable(this.limitBids.poll());
    }

    public Optional<OrderEntry> asksPoll() {
        if (!this.marketAsks.isEmpty()) {
            return Optional.ofNullable(this.marketAsks.poll());
        }

        return Optional.ofNullable(this.limitAsks.poll());
    }

    public MatchResult match(PriceType priceType, OrderType orderType) {
        switch (priceType) {
            case LIMIT:
                return matchLimitOrder();
            case MARKET:
//                return matchMarketOrder(orderType);
            default:
                // pass
                return null;
        }
    }

    private MatchResult matchLimitOrder() {
        OrderEntry bid = limitBids.peek();
        OrderEntry ask = limitAsks.peek();
        if (ask == null || bid == null) {
            return null;
        }

        int c = bid.getPrice().compareTo(ask.getPrice());
        int d = bid.shares() - ask.shares();
        // matched
        if (c >= 0) {
            if (d == 0) { // totally matched
                limitBids.poll();
                limitAsks.poll();
                return MatchResult.exact(bid, symbol, ask);
            } else if (d > 0) { // need more ask
                List<OrderEntry> temp = new ArrayList<>();
                temp.add(limitAsks.poll());
                while (d > 0) {
                    OrderEntry peek = limitAsks.peek();
                    if (peek == null) return null;
                    if (d < peek.shares()) {
                        peek.partialMatched(bid, d);
                        temp.add(peek);
                        d = 0;
                    } else if (d > peek.shares()) {
                        d -= peek.shares();
                        temp.add(limitAsks.poll());
                    } else { // d == peek.shares()
                        temp.add(limitAsks.poll());
                        break;
                    }
                }

                return MatchResult.bigBid(bid, symbol, temp);
            } else { // ask has more shares
                ask.partialMatched(bid);
                limitBids.poll();
                return MatchResult.bigAsk(bid, symbol, ask);
            }
        } else { // not matched
            return null;
        }
    }

//    private MatchResult matchMarketOrder(OrderType orderType) {
//        switch (orderType) {
//            case BID:
//                    return null;
//            case ASK:
//                return null;
//            default:
//                return null;
//        }
//    }


}
