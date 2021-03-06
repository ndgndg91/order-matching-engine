package com.ndgndg91.ordermatchingengine.order;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
public class MatchedEntry {
    private final String orderId;
    private final OrderType orderType;
    private final int shares;
    private final PriceType priceType;
    private final BigDecimal price;
    private final LocalDateTime timestamp;

    public MatchedEntry(OrderEntry e, int shares) {
        this.orderId = e.getOrderId();
        this.orderType = e.getOrderType();
        this.shares = shares;
        this.priceType = e.getPriceType();
        this.price = e.getPrice();
        this.timestamp = e.getTimestamp();
    }

    public MatchedEntry(OrderEntry e, String orderId) {
        this.orderId = e.getOrderId();
        this.orderType = e.getOrderType();
        this.shares = e.partialShares(orderId);
        this.priceType = e.getPriceType();
        this.price = e.getPrice();
        this.timestamp = e.getTimestamp();
    }

    public BigDecimal totalPrice() {
        return price.multiply(BigDecimal.valueOf(shares));
    }
}
