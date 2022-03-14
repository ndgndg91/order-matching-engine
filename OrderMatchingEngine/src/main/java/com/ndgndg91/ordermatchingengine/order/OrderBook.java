package com.ndgndg91.ordermatchingengine.order;

import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.concurrent.ConcurrentSkipListSet;

@Slf4j
public class OrderBook {
    private final Symbol symbol;
    private final SortedSet<OrderEntry> limitBids;
    private final SortedSet<OrderEntry> limitAsks;
    private final SortedSet<OrderEntry> marketBids;
    private final SortedSet<OrderEntry> marketAsks;

    public OrderBook(Symbol symbol) {
        this.symbol = symbol;
        this.limitBids = new ConcurrentSkipListSet<>((e1, e2) -> {
            int c = e2.getPrice().compareTo(e1.getPrice());
            if (c == 0) return e1.getTimestamp().compareTo(e2.getTimestamp());
            return c;
        });
        this.limitAsks = new ConcurrentSkipListSet<>((e1, e2) -> {
            int c = e1.getPrice().compareTo(e2.getPrice());
            if (c == 0) return e1.getTimestamp().compareTo(e2.getTimestamp());
            return c;
        });
        this.marketBids = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEntry::getTimestamp));
        this.marketAsks = new ConcurrentSkipListSet<>(Comparator.comparing(OrderEntry::getTimestamp));
    }

    private SortedSet<OrderEntry> selectQueue(PriceType priceType, OrderType orderType) {
        switch (priceType) {
            case MARKET:
                return marketQueue(orderType);
            case LIMIT:
                return limitQueue(orderType);
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found appropriate queue");
        }
    }

    private SortedSet<OrderEntry> limitQueue(OrderType orderType) {
        switch (orderType) {
            case BID:
                return this.limitBids;
            case ASK:
                return this.limitAsks;
            default:
                throw new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "not found appropriate queue");
        }
    }

    private SortedSet<OrderEntry> marketQueue(OrderType orderType) {
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
        SortedSet<OrderEntry> queue = selectQueue(order.getPriceType(), order.getOrderType());
        if (queue.stream().parallel().anyMatch(e -> e.getOrderId().equals(order.getOrderId()))) {
            log.info("already exists, failed to add {}", order);
        } else {
            OrderEntry e = new OrderEntry(order);
            queue.add(e);
            log.info("success to add {}", order);
        }
    }

    public void modifyOrder(Order order) {
        SortedSet<OrderEntry> queue = selectQueue(order.getPriceType(), order.getOrderType());
        OrderEntry oe = queue.stream()
                .parallel()
                .filter(e -> e.getOrderId().equals(order.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when modifying order, not found order " + order.getOrderId()));
        queue.remove(oe);
        queue.add(new OrderEntry(order));
    }

    public void cancelOrder(Order order) {
        SortedSet<OrderEntry> queue = selectQueue(order.getPriceType(), order.getOrderType());

        OrderEntry target = queue.stream()
                .parallel()
                .filter(e -> e.getOrderId().equals(order.getOrderId()))
                .findFirst()
                .orElseThrow(() -> new OrderServiceException(null, HttpStatus.BAD_REQUEST.value(), "when canceling order, not found order : " + order.getOrderId()));
        queue.remove(target);
    }

    public Optional<OrderEntry> bidsPoll() {
        if (!this.marketBids.isEmpty()) {
            return Optional.ofNullable(poll(this.marketBids));
        }

        return Optional.ofNullable(poll(this.limitBids));
    }

    public Optional<OrderEntry> asksPoll() {
        if (!this.marketAsks.isEmpty()) {
            return Optional.ofNullable(poll(this.marketAsks));
        }

        return Optional.ofNullable(poll(this.limitAsks));
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
        OrderEntry bid = peek(this.limitBids);
        OrderEntry ask = peek(this.limitAsks);
        if (ask == null || bid == null) {
            return null;
        }

        int c = bid.getPrice().compareTo(ask.getPrice());
        int d = bid.shares() - ask.shares();
        // matched
        if (c >= 0) {
            if (d == 0) { // totally matched
                poll(this.limitBids);
                poll(this.limitAsks);
                return MatchResult.exact(bid, symbol, ask);
            } else if (d > 0) { // need more ask
                List<OrderEntry> tAsks = new ArrayList<>();
                tAsks.add(poll(this.limitAsks));
                while (d > 0) {
                    OrderEntry peek = peek(this.limitAsks);
                    if (peek == null || bid.getPrice().compareTo(peek.getPrice()) < 0) {
                        limitAsks.addAll(tAsks);
                        return null;
                    }

                    if (d < peek.shares()) {
                        peek.partialMatched(bid, d);
                        tAsks.add(peek);
                        poll(this.limitBids);
                        d = 0;
                    } else if (d > peek.shares()) {
                        d -= peek.shares();
                        tAsks.add(poll(this.limitAsks));
                    } else { // d == peek.shares()
                        tAsks.add(poll(this.limitAsks));
                        break;
                    }
                }

                return MatchResult.bigBid(bid, symbol, tAsks);
            } else { // ask has more shares
                ask.partialMatched(bid);
                poll(this.limitBids);
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
        OrderEntry mBid = peek(this.marketBids);
        OrderEntry lAsk = peek(this.limitAsks);
        if (mBid == null || lAsk == null) {
            return null;
        }

        int d = mBid.shares() - lAsk.shares();
        if (d == 0) { // exact
            poll(this.marketBids);
            poll(this.limitAsks);
            return MatchResult.exact(mBid, symbol, lAsk);
        } else if (d > 0) { // need more ask
            List<OrderEntry> tAsks = new ArrayList<>();
            tAsks.add(poll(this.limitAsks));
            while (d > 0) {
                OrderEntry peek = peek(this.limitAsks);
                if (peek == null) {
                    limitAsks.addAll(tAsks);
                    return null;
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mBid, d);
                    tAsks.add(peek);
                    poll(this.marketBids);
                    d = 0;
                } else if (d > peek.shares()) {
                    d -= peek.shares();
                    tAsks.add(poll(this.limitAsks));
                } else { // d == peek.shares()
                    tAsks.add(poll(this.limitAsks));
                    break;
                }
            }

            return MatchResult.bigBid(mBid, symbol, tAsks);
        } else { // ask partial match
            lAsk.partialMatched(mBid);
            poll(this.marketBids);
            return MatchResult.bigAsk(mBid, symbol, lAsk);
        }
    }

    private MatchResult matchMarketAskOrder() {
        OrderEntry mAsk = peek(this.marketAsks);
        OrderEntry lBid = peek(this.limitBids);
        if (mAsk == null || lBid == null) {
            return null;
        }

        int d = mAsk.shares() - lBid.shares();
        if (d == 0) {
            poll(this.marketAsks);
            poll(this.limitBids);
            return MatchResult.exact(mAsk, symbol, lBid);
        } else if (d > 0) { // need more bid
            List<OrderEntry> tBids = new ArrayList<>();
            tBids.add(poll(this.limitBids));
            while (d > 0) {
                OrderEntry peek = peek(this.limitBids);
                if (peek == null) {
                    limitBids.addAll(tBids);
                    return null;
                }

                if (d < peek.shares()) {
                    peek.partialMatched(mAsk, d);
                    tBids.add(peek);
                    poll(this.marketBids);
                    d = 0;
                } else if (d > peek.shares()) {
                    d -= peek.shares();
                    tBids.add(poll(this.limitBids));
                } else { // d == peek.shares()
                    tBids.add(poll(this.limitBids));
                    break;
                }
            }

            return MatchResult.bigBid(mAsk, symbol, tBids);
        } else { // bid partial match
            lBid.partialMatched(mAsk);
            poll(this.marketAsks);
            return MatchResult.bigAsk(mAsk, symbol, lBid);
        }
    }

    private OrderEntry peek(SortedSet<OrderEntry> queue) {
        try {
            return queue.first();
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    private OrderEntry poll(SortedSet<OrderEntry> queue) {
        try {
            OrderEntry first = queue.first();
            queue.remove(first);
            return first;
        } catch (NoSuchElementException e) {
            return null;
        }
    }
}
