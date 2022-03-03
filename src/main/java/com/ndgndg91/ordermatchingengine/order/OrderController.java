package com.ndgndg91.ordermatchingengine.order;

import com.ndgndg91.ordermatchingengine.global.ApiResponse;
import com.ndgndg91.ordermatchingengine.global.OrderServiceException;
import com.ndgndg91.ordermatchingengine.order.dto.request.AddOrderRequest;
import com.ndgndg91.ordermatchingengine.order.dto.response.OrderBookResponse;
import com.ndgndg91.ordermatchingengine.order.service.Engine;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final Engine engine;

    @GetMapping("/apis/order-books/{symbol}")
    public ResponseEntity<ApiResponse<OrderBookResponse>> orderBook(
            final HttpServletRequest request,
            @PathVariable final Symbol symbol
    ) {
        final var orderBook = engine.findOrderBookBySymbol(symbol)
                .orElseThrow(() -> new OrderServiceException(
                        request.getRequestURI(),
                        HttpStatus.NOT_FOUND.value(),
                        "Not Found order book by " + symbol)
                );
        OrderBookResponse response = new OrderBookResponse(symbol.name(), orderBook);
        return ResponseEntity.ok(new ApiResponse<>(response));
    }

    @PostMapping("/apis/orders")
    public ResponseEntity<ApiResponse<Void>> newOrder(
            @RequestBody final AddOrderRequest request
    ) {
//        engine.addOrder();
        return ResponseEntity.created(URI.create("")).build();
    }
}
