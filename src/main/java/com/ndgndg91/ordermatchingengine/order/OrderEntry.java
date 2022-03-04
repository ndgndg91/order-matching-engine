package com.ndgndg91.ordermatchingengine.order;

import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@ToString
public class OrderEntry {
    private final String orderId;
    private final int shares;
    private final PriceType priceType;
    private final BigDecimal price;
    private final LocalDateTime timestamp;

    public OrderEntry(final Order order) {
        this.orderId = order.getOrderId();
        this.shares = order.getShares();
        this.priceType = order.getPriceType();
        this.price = order.getPrice();
        this.timestamp = order.getTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderEntry that = (OrderEntry) o;
        return Objects.equals(orderId, that.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId);
    }
}
