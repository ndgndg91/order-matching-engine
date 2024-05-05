package com.ndgndg91.ordermatchingengine.order.dto.request;

import com.ndgndg91.ordermatchingengine.order.OrderType;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import com.ndgndg91.ordermatchingengine.order.validation.ValueOfEnum;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.NotNull;

@Getter
@ToString
public final class CancelOrderRequest {
    @NotNull(message = "order id is required")
    private String orderId;
    @ValueOfEnum(enumClass = OrderType.class)
    @NotNull(message = "order type is required")
    private String orderType;
    @ValueOfEnum(enumClass = Symbol.class)
    @NotNull(message = "symbol is required")
    private String symbol;
}
