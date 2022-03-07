package com.ndgndg91.ordermatchingengine.order;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class MatchResult {
    private final Symbol symbol;
    private final String orderId;
    private final OrderType orderType;
    private final int shares;
    private final PriceType priceType;
    private final BigDecimal price;
    private final LocalDateTime timestamp;
    private final List<MatchedEntry> matchedEntries = new ArrayList<>();

    public static MatchResult exact(OrderEntry e, Symbol symbol, OrderEntry t) {
        return new MatchResult(e, symbol, t);
    }

    public static MatchResult bigAsk(OrderEntry e, Symbol symbol, OrderEntry p) {
        return new MatchResult(e, symbol, p);
    }

    public static MatchResult bigBid(OrderEntry e, Symbol symbol, List<OrderEntry> asks) {
        return new MatchResult(e, symbol, asks);
    }

    private MatchResult(OrderEntry e, Symbol symbol, OrderEntry t) {
        this.symbol = symbol;
        this.orderId = e.getOrderId();
        this.orderType = e.getOrderType();
        this.shares = e.shares();
        this.priceType = e.getPriceType();
        this.price = e.getPrice();
        this.timestamp = e.getTimestamp();
        this.matchedEntries.add(new MatchedEntry(t, e.shares()));
    }

    private MatchResult(OrderEntry e, Symbol symbol, List<OrderEntry> asks) {
        this.symbol = symbol;
        this.orderId = e.getOrderId();
        this.orderType = e.getOrderType();
        this.shares = e.shares();
        this.priceType = e.getPriceType();
        this.price = e.getPrice();
        this.timestamp = e.getTimestamp();
        List<MatchedEntry> entries = asks.stream().map(MatchedEntry::new).collect(Collectors.toList());
        this.matchedEntries.addAll(entries);
    }

    public boolean matchedShare() {
        return shares == matchedEntries.stream().mapToInt(MatchedEntry::getShares).sum();
    }
}
