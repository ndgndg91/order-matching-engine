package com.ndgndg91.ordermatchingengine.order.dto.request;

import com.ndgndg91.ordermatchingengine.order.OrderType;
import com.ndgndg91.ordermatchingengine.order.PriceType;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;

@Getter
@ToString
public final class AddOrderRequest {
    private OrderType orderType;
    private Symbol symbol;
    private int shares;
    private PriceType priceType;
    private BigDecimal price;
}
