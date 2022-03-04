package com.ndgndg91.ordermatchingengine.order.dto.response;

import com.ndgndg91.ordermatchingengine.order.OrderEntry;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@ToString
public final class OrderEntryResponse {
    private final String orderId;
    private final int shares;
    private final String priceType;
    private final BigDecimal price;
    private final LocalDateTime timestamp;

    public OrderEntryResponse(final OrderEntry orderEntry) {
        this.orderId = orderEntry.getOrderId();
        this.shares = orderEntry.getShares();
        this.priceType = orderEntry.getPriceType().name();
        this.price = orderEntry.getPrice();
        this.timestamp = orderEntry.getTimestamp();
    }
}
