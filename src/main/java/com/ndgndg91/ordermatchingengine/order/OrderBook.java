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
        this.limitBids = new PriorityBlockingQueue<>(100, (e1, e2) -> {
            int c = e2.getPrice().compareTo(e1.getPrice());
            if (c == 0) return e1.getTimestamp().compareTo(e2.getTimestamp());
            return c;
        });
        this.limitAsks = new PriorityBlockingQueue<>(100, (e1, e2) -> {
            int c = e1.getPrice().compareTo(e2.getPrice());
            if (c == 0) return e1.getTimestamp().compareTo(e2.getTimestamp());
            return c;
        });
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
                return matchMarketOrder(orderType);
            default:
                // must do not happen
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
                List<OrderEntry> tAsks = new ArrayList<>();
                tAsks.add(limitAsks.poll());
                while (d > 0) {
                    OrderEntry peek = limitAsks.peek();
                    if (peek == null) {
                        limitAsks.addAll(tAsks);
                        return null;
                    }

                    if (d < peek.shares()) {
                        peek.partialMatched(bid, d);
                        tAsks.add(peek);
                        limitBids.poll();
                        d = 0;
                    } else if (d > peek.shares()) {
                        d -= peek.shares();
                        tAsks.add(limitAsks.poll());
                    } else { // d == peek.shares()
                        tAsks.add(limitAsks.poll());
                        break;
                    }
                }

                return MatchResult.bigBid(bid, symbol, tAsks);
            } else { // ask has more shares
                ask.partialMatched(bid);
                limitBids.poll();
                return MatchResult.bigAsk(bid, symbol, ask);
            }
        } else { // not matched
            return null;
        }
    }

    private MatchResult matchMarketOrder(OrderType orderType) {
        switch (orderType) {
            case BID:
                return matchMarketBidOrder();
            case ASK:
                return matchMarketAskOrder();
            default:
                // must do not happen
                return null;
        }
    }

    private MatchResult matchMarketBidOrder() {
        OrderEntry mBid = this.marketBids.peek();
        OrderEntry lAsk = this.limitAsks.peek();
        if (mBid == null || lAsk == null) {
            return null;
        }

        int d = mBid.shares() - lAsk.shares();
        if (d == 0) { // exact
            this.marketBids.poll();
            this.limitAsks.poll();
            return MatchResult.exact(mBid, symbol, lAsk);
        } else if (d > 0) { // need more ask
            List<OrderEntry> tAsks = new ArrayList<>();
            tAsks.add(this.limitAsks.poll());
            while (d > 0) {
                OrderEntry peek = this.limitAsks.peek();
                if (peek == null) {
                    limitAsks.addAll(tAsks);
                    return null;
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mBid, d);
                    tAsks.add(peek);
                    this.marketBids.poll();
                    d = 0;
                } else if (d > peek.shares()) {
                    d -= peek.shares();
                    tAsks.add(limitAsks.poll());
                } else { // d == peek.shares()
                    tAsks.add(limitAsks.poll());
                    break;
                }
            }

            return MatchResult.bigBid(mBid, symbol, tAsks);
        } else { // ask partial match
            lAsk.partialMatched(mBid);
            this.marketBids.poll();
            return MatchResult.bigAsk(mBid, symbol, lAsk);
        }
    }

    private MatchResult matchMarketAskOrder() {
        OrderEntry mAsk = this.marketAsks.peek();
        OrderEntry lBid = this.limitBids.peek();
        if (mAsk == null || lBid == null) {
            return null;
        }

        int d = mAsk.shares() - lBid.shares();
        if (d == 0) {
            this.marketAsks.poll();
            this.limitBids.poll();
            return MatchResult.exact(mAsk, symbol, lBid);
        } else if (d > 0) { // need more bid
            List<OrderEntry> tBids = new ArrayList<>();
            tBids.add(this.limitBids.poll());
            while (d > 0) {
                OrderEntry peek = this.limitBids.peek();
                if (peek == null) {
                    limitBids.addAll(tBids);
                    return null;
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mAsk, d);
                    tBids.add(peek);
                    this.marketBids.poll();
                    d = 0;
                } else if (d > peek.shares()) {
                    d -= peek.shares();
                    tBids.add(limitBids.poll());
                } else { // d == peek.shares()
                    tBids.add(limitBids.poll());
                    break;
                }
            }

            return MatchResult.bigBid(mAsk, symbol, tBids);
        } else { // bid partial match
            lBid.partialMatched(mAsk);
            this.marketAsks.poll();
            return MatchResult.bigAsk(mAsk, symbol, lBid);
        }
    }


}
