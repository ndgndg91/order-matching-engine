package com.ndgndg91.ordermatchingenginekotlin.order.service

import com.ndgndg91.ordermatchingenginekotlin.order.*
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.AddOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.CancelOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.dto.request.ModifyOrderRequest
import com.ndgndg91.ordermatchingenginekotlin.order.event.OrderMatchTriggerEvent
import com.ndgndg91.ordermatchingenginekotlin.order.repository.MatchOrderRepository
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.util.*

@Service
class Engine(
    private val orderBooks: OrderBooks,
    private val matchOrderRepository: MatchOrderRepository,
    private val publisher: ApplicationEventPublisher
) {
    private val log: Logger = LoggerFactory.getLogger(Engine::class.java)
    fun addOrder(request: AddOrderRequest): Order {
        log.info("add order request : {}", request)
        val order = Order.Builder()
            .orderId(UUID.randomUUID().toString())
            .orderType(OrderType.valueOf(request.orderType!!))
            .priceType(PriceType.valueOf(request.priceType!!))
            .price(request.price!!)
            .symbol(Symbol.valueOf(request.symbol!!))
            .shares(request.shares!!)
            .build()
        log.info("add order : {}", order)
        orderBooks.addOrder(order)
        publisher.publishEvent(OrderMatchTriggerEvent(order.symbol, order.priceType, order.orderType))
        return order
    }

    fun modifyOrder(request: ModifyOrderRequest) {
        log.info("modify order request : {}", request)
        val order = Order.Builder()
            .orderId(request.orderId!!)
            .orderType(OrderType.valueOf(request.orderType!!))
            .priceType(PriceType.valueOf(request.priceType!!))
            .price(request.price!!)
            .symbol(Symbol.valueOf(request.symbol!!))
            .shares(request.shares!!)
            .build()
        log.info("modify order : {}", order)
        orderBooks.modifyOrder(order)
        publisher.publishEvent(OrderMatchTriggerEvent(order.symbol, order.priceType, order.orderType))
    }

    fun cancelOrder(request: CancelOrderRequest) {
        log.info("cancel order request : {}", request)
        val order = Order.Builder()
            .orderId(request.orderId!!)
            .orderType(OrderType.valueOf(request.orderType!!))
            .symbol(Symbol.valueOf(request.symbol!!))
            .build()
        log.info("cancel order : {}", order)
        publisher.publishEvent(OrderMatchTriggerEvent(order.symbol, order.priceType, order.orderType))
        orderBooks.cancelOrder(order)
    }

    fun find(symbol: String, orderType: String, orderId: String): OrderEntry? {
        return orderBooks.find(Symbol.valueOf(symbol), OrderType.valueOf(orderType), orderId)
    }

    fun test(symbol: String): MutableList<String>? {
        return matchOrderRepository.findAllBySymbol(symbol)
    }

    @Async
    @EventListener
    fun match(event: OrderMatchTriggerEvent) {
        log.info("{}", event)
        orderBooks.match(event.symbol, event.priceType, event.orderType)
            .also { log.info("$it"); }
            .let { matchOrderRepository.save(event.symbol, it) }
    }
}