package com.ndgndg91.ordermatchingengine.order.event;

import com.ndgndg91.ordermatchingengine.order.OrderType;
import com.ndgndg91.ordermatchingengine.order.PriceType;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public final class OrderMatchTriggerEvent {
    private final Symbol symbol;
    private final PriceType priceType;
    private final OrderType orderType;
    private final LocalDateTime creationTime;

    public OrderMatchTriggerEvent(Symbol symbol, PriceType priceType, OrderType orderType) {
        this.symbol = symbol;
        this.priceType = priceType;
        this.orderType = orderType;
        this.creationTime = LocalDateTime.now();
    }
}
