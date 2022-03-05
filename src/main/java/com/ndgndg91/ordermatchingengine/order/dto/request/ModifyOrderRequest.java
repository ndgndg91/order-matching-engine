package com.ndgndg91.ordermatchingengine.order.dto.request;

import com.ndgndg91.ordermatchingengine.order.OrderType;
import com.ndgndg91.ordermatchingengine.order.PriceType;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import com.ndgndg91.ordermatchingengine.order.validation.ValueOfEnum;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.*;
import java.math.BigDecimal;

@Getter
@ToString
public final class ModifyOrderRequest {
    @NotNull(message = "order id is required")
    private String orderId;
    @ValueOfEnum(enumClass = OrderType.class)
    @NotNull(message = "order type is required")
    private String orderType;
    @ValueOfEnum(enumClass = Symbol.class)
    @NotNull(message = "symbol is required")
    private String symbol;
    @Min(1)
    @Max(Integer.MAX_VALUE)
    @NotNull(message = "shares is required")
    private Integer shares;
    @ValueOfEnum(enumClass = PriceType.class)
    @NotNull(message = "price type is required")
    private String priceType;
    @DecimalMin("0.0")
    @DecimalMax("9999999999.99")
    @Digits(integer = 10, fraction = 2)
    private BigDecimal price;
}
