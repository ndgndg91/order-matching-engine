package com.ndgndg91.ordermatchingengine.order.dto.response;

import com.ndgndg91.ordermatchingengine.order.OrderBook;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@ToString
public class OrderBookResponse {
    private final String symbol;
    private final List<OrderEntryResponse> bids;
    private final List<OrderEntryResponse> asks;

    public OrderBookResponse(final String symbol, final OrderBook orderBook) {
        this.symbol = symbol;
        this.bids = orderBook.bidsToList()
                .stream()
                .map(OrderEntryResponse::new)
                .collect(Collectors.toList());
        this.asks = orderBook.asksToList()
                .stream()
                .map(OrderEntryResponse::new)
                .collect(Collectors.toList());
    }
}
