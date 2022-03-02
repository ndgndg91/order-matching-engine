package com.ndgndg91.ordermatchingengine.order;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@EqualsAndHashCode(of = "orderId")
public class OrderEntry {
    private final long orderId;
    private int shares;
    private PriceType priceType;
    private BigDecimal price;

    public OrderEntry(final Order order) {
        this.orderId = order.getOrderId();
        this.shares = order.getShares();
        this.priceType = order.getPriceType();
        this.price = order.getPrice();
    }
}
