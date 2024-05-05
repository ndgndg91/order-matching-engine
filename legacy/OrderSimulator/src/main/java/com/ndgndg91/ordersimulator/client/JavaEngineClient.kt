package com.ndgndg91.ordersimulator.client

import com.ndgndg91.ordersimulator.client.request.AddOrderRequest
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody

@FeignClient(
    name = "javaEngine",
    url = "http://localhost:8080",
    configuration = [JavaEngineClientConfiguration::class]
)
interface JavaEngineClient {

    @PostMapping("/apis/orders")
    fun addOrder(@RequestBody addOrderRequest: AddOrderRequest)
}