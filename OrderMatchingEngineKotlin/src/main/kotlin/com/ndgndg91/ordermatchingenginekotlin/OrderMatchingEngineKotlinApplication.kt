package com.ndgndg91.ordermatchingenginekotlin

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableAsync

@EnableAsync
@SpringBootApplication
class OrderMatchingEngineKotlinApplication

fun main(args: Array<String>) {
    runApplication<OrderMatchingEngineKotlinApplication>(*args)
}
