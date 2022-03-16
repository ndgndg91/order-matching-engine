package com.ndgndg91.ordermatchingenginekotlin.order.controller

import com.ndgndg91.ordermatchingenginekotlin.global.ApiResponse
import com.ndgndg91.ordermatchingenginekotlin.global.OrderServiceException
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.AddOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.CancelOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.ModifyOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.response.OrderEntryResponse
import com.ndgndg91.ordermatchingenginekotlin.order.service.Engine
import com.ndgndg91.ordermatchingenginekotlin.order.validation.OrderTypeValue
import com.ndgndg91.ordermatchingenginekotlin.order.validation.SymbolValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*
import java.net.URI
import javax.validation.Valid
import javax.validation.constraints.NotNull

@Validated
@RestController
class OrderController(private val engine: Engine, private val redisTemplate: RedisTemplate<String, String>) {

    private val log: Logger = LoggerFactory.getLogger(OrderController::class.java)

    @PostMapping("/apis/orders")
    fun newOrder(@Valid @RequestBody  request: AddOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        log.info("{}", request)
        val order = engine.addOrder(request)
        return ResponseEntity
            .created(URI.create("/apis/orders/" + order.symbol + "/" + order.orderType + "/" + order.orderId))
            .build()
    }

    @PatchMapping("/apis/orders")
    fun modifyOrder(@Valid @RequestBody request: ModifyOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        log.info("{}", request)
        engine.modifyOrder(request)
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/apis/orders")
    fun cancelOrder(@Valid @RequestBody request: CancelOrderRequest): ResponseEntity<ApiResponse<Unit>> {
        engine.cancelOrder(request)
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/apis/orders/{symbol}/{orderType}/{orderId}")
    fun findOrder(
        @PathVariable @SymbolValue symbol: String,
        @PathVariable @OrderTypeValue orderType: String,
        @PathVariable @NotNull(message = "order id is required") orderId: String
    ) :ResponseEntity<ApiResponse<OrderEntryResponse>> {
        val result = kotlin.runCatching { engine.find(symbol, orderType, orderId)!! }
        if (result.isFailure) {
            throw OrderServiceException(
                null,
                HttpStatus.NOT_FOUND.value(),
                String.format("Not Found symbol: % order type : %s, order id : %s", symbol, orderType, orderId)
            )
        }
        val e = result.getOrThrow()
        return ResponseEntity.ok(ApiResponse(OrderEntryResponse(e)))
    }

    @PostMapping("/apis/redis/{key}/{value}")
    fun redisSet(
        @PathVariable key: String,
        @PathVariable value: String
    ): ResponseEntity<ApiResponse<Unit>> {
        redisTemplate.opsForValue().set(key, value)
        return ResponseEntity.ok().build()
    }

    @GetMapping("/apis/redis/{key}")
    fun redisGet(
        @PathVariable key: String
    ): ResponseEntity<ApiResponse<String>> {
        val maybe = kotlin.runCatching { redisTemplate.opsForValue().get(key)!! }
        if (maybe.isFailure) {
            throw OrderServiceException(null, HttpStatus.NOT_FOUND.value(), String.format("Not Found value by %s", key))
        }
        return ResponseEntity.ok(ApiResponse(maybe.getOrThrow()))
    }

//    @GetMapping("/apis/orders/bids/{symbol}/poll")
//    fun bidEntry(@PathVariable symbol: String): ResponseEntity<ApiResponse<?>> {
//        return
//    }
}