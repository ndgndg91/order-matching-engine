package com.ndgndg91.ordermatchingengine.order;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@ToString
public class OrderEntry {
    private final String orderId;
    private final OrderType orderType;
    private final int shares;
    private final PriceType priceType;
    private final BigDecimal price;
    private final LocalDateTime timestamp;
    private boolean partialMatched;
    private final List<PartialMatched> partialMatchedEntries = new ArrayList<>();

    public OrderEntry(final Order order) {
        this.orderId = order.getOrderId();
        this.orderType = order.getOrderType();
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

    public void partialMatched(final OrderEntry e) {
        this.partialMatched = true;
        PartialMatched pm = new PartialMatched(
                e.orderId,
                e.orderType,
                e.shares(),
                e.priceType,
                e.price,
                e.timestamp
        );
        this.partialMatchedEntries.add(pm);
    }

    public void partialMatched(final OrderEntry e, final int shares) {
        this.partialMatched = true;
        PartialMatched pm = new PartialMatched(
                e.orderId,
                e.orderType,
                shares,
                e.priceType,
                e.price,
                e.timestamp
        );
        this.partialMatchedEntries.add(pm);
    }

    public int shares() {
        if (partialMatched) {
            return shares - partialMatchedEntries.stream().parallel().mapToInt(p -> p.shares).sum();
        } else {
            return shares;
        }
    }

    public int partialShares() {
        return partialMatchedEntries.stream().parallel().mapToInt(p -> p.shares).sum();
    }

    @RequiredArgsConstructor
    @ToString
    private static final class PartialMatched {
        private final String orderId;
        private final OrderType orderType;
        private final int shares;
        private final PriceType priceType;
        private final BigDecimal price;
        private final LocalDateTime timestamp;
    }
}
