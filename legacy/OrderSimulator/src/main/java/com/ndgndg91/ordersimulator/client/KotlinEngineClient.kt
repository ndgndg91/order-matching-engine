package com.ndgndg91.ordersimulator.client

import com.ndgndg91.ordersimulator.client.request.AddOrderRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "kotlinEngine",
    url = "http://localhost:9090",
    configuration = [KotlinEngineClientConfiguration::class]
)
interface KotlinEngineClient {

    @PostMapping("/apis/orders")
    fun addOrder(@RequestBody addOrderRequest: AddOrderRequest)
}