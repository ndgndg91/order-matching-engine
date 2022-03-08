package com.ndgndg91.ordermatchingengine.order;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.math.RoundingMode;
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

    public static MatchResult exact(OrderEntry bid, Symbol symbol, OrderEntry ask) {
        return new MatchResult(bid, symbol, ask);
    }

    public static MatchResult bigAsk(OrderEntry bid, Symbol symbol, OrderEntry ask) {
        return new MatchResult(bid, symbol, ask);
    }

    public static MatchResult bigBid(OrderEntry bid, Symbol symbol, List<OrderEntry> ask) {
        return new MatchResult(bid, symbol, ask);
    }

    private MatchResult(OrderEntry bid, Symbol symbol, OrderEntry ask) {
        this.symbol = symbol;
        this.orderId = bid.getOrderId();
        this.orderType = bid.getOrderType();
        this.shares = bid.shares();
        this.priceType = bid.getPriceType();
        this.price = bid.getPrice();
        this.timestamp = bid.getTimestamp();
        this.matchedEntries.add(new MatchedEntry(ask, bid.shares()));
    }

    private MatchResult(OrderEntry bid, Symbol symbol, List<OrderEntry> asks) {
        this.symbol = symbol;
        this.orderId = bid.getOrderId();
        this.orderType = bid.getOrderType();
        this.shares = bid.shares();
        this.priceType = bid.getPriceType();
        this.price = bid.getPrice();
        this.timestamp = bid.getTimestamp();
        List<MatchedEntry> entries = asks.stream().map(MatchedEntry::new).collect(Collectors.toList());
        this.matchedEntries.addAll(entries);
    }

    public int matchedShare() {
        return matchedEntries.stream().mapToInt(MatchedEntry::getShares).sum();
    }

    public BigDecimal averagePrice() {
        return this.matchedEntries.stream()
                .map(MatchedEntry::totalPrice).reduce(BigDecimal.ZERO, BigDecimal::add)
                .divide(BigDecimal.valueOf(this.shares), RoundingMode.CEILING);
    }
}
