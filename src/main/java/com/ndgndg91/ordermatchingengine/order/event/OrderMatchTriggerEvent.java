package com.ndgndg91.ordermatchingengine.order.event;

import com.ndgndg91.ordermatchingengine.order.Symbol;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
public final class OrderMatchTriggerEvent {
    private final Symbol symbol;
    private final LocalDateTime creationTime;

    public OrderMatchTriggerEvent(Symbol symbol) {
        this.symbol = symbol;
        this.creationTime = LocalDateTime.now();
    }
}
