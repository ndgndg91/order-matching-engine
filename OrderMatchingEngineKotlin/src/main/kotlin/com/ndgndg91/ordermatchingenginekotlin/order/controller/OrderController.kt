package com.ndgndg91.ordermatchingenginekotlin.order.controller

import com.ndgndg91.ordermatchingenginekotlin.global.ApiResponse
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.AddOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.CancelOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.ModifyOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.service.Engine
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid

@Validated
@RestController
class OrderController(private val engine: Engine) {

    private val log: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping("/apis/orders")
    fun newOrder(@RequestBody @Valid request: AddOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        log.info("{}", request)
        val order = engine.addOrder(request)
        return ResponseEntity
            .created(URI.create("/apis/orders/" + order.symbol + "/" + order.orderType + "/" + order.orderId))
            .build()
    }

    @PatchMapping("/apis/orders")
    fun modifyOrder(@RequestBody @Valid request: ModifyOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        log.info("{}", request)
        engine.modifyOrder(request)
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/apis/orders")
    fun cancelOrder(@RequestBody @Valid request: CancelOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        engine.cancelOrder(request)
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/apis/orders/{symbol}/{orderType}/{orderId}")
    fun findOrder(
        @PathVariable symbol: String,
        @PathVariable orderType: String,
        @PathVariable orderId: String
    ) :ResponseEntity<ApiResponse<Unit>> {
        val e = engine.find(symbol, orderType, orderId)
        return ResponseEntity.ok().build();
    }

//    @GetMapping("/apis/orders/bids/{symbol}/poll")
//    fun bidEntry(@PathVariable symbol: String): ResponseEntity<ApiResponse<?>> {
//        return
//    }
}