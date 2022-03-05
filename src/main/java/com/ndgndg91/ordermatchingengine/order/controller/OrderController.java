package com.ndgndg91.ordermatchingengine.order.controller;

import com.ndgndg91.ordermatchingengine.global.ApiResponse;
import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import com.ndgndg91.ordermatchingengine.order.OrderEntry;
import com.ndgndg91.ordermatchingengine.order.Symbol;
import com.ndgndg91.ordermatchingengine.order.dto.request.AddOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.request.CancelOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.request.ModifyOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.response.OrderEntryResponse;
import com.ndgndg91.ordermatchingengine.order.service.Engine;
import com.ndgndg91.ordermatchingengine.order.validation.ValueOfEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.net.URI;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final Engine engine;

    @PostMapping("/apis/orders")
    public ResponseEntity<ApiResponse<Void>> newOrder(
            @RequestBody @Valid final AddOrderRequest request
    ) {
        engine.addOrder(request);
        return ResponseEntity.created(URI.create("/apis/order-books/" + request.getSymbol())).build();
    }

    @PatchMapping("/apis/orders")
    public ResponseEntity<ApiResponse<Void>> modifyOrder(
            @RequestBody @Valid final ModifyOrderRequest request
    ) {
        engine.modifyOrder(request);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/apis/orders")
    public ResponseEntity<ApiResponse<Void>> cancelOrder(
            @RequestBody @Valid final CancelOrderRequest request
    ) {
        engine.cancelOrder(request);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/apis/orders/bids/{symbol}/poll")
    public ResponseEntity<ApiResponse<OrderEntryResponse>> bidEntry(
            @PathVariable @ValueOfEnum(enumClass = Symbol.class) final String symbol,
            final HttpServletRequest request
    ) {
        OrderEntry orderEntry = engine.pollBids(Symbol.valueOf(symbol))
                .orElseThrow(() -> new OrderServiceException(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "not found bid entry by : " + symbol));
        OrderEntryResponse response = new OrderEntryResponse(orderEntry);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @GetMapping("/apis/orders/asks/{symbol}/poll")
    public ResponseEntity<ApiResponse<OrderEntryResponse>> askEntry(
        @PathVariable @ValueOfEnum(enumClass = Symbol.class) final String symbol,
        final HttpServletRequest request
    ) {
        OrderEntry orderEntry = engine.pollAsks(Symbol.valueOf(symbol))
                .orElseThrow(() -> new OrderServiceException(request.getRequestURI(), HttpStatus.BAD_REQUEST.value(), "not found ask entry by : " + symbol));
        OrderEntryResponse response = new OrderEntryResponse(orderEntry);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }
}
