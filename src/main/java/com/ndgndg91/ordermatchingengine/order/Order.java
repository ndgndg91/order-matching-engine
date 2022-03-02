package com.ndgndg91.ordermatchingengine.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
@Builder
public class Order {
    private long orderId;
    private OrderType orderType;
    private Symbol symbol;
    private int shares;
    private PriceType priceType;
    private BigDecimal price;
}
