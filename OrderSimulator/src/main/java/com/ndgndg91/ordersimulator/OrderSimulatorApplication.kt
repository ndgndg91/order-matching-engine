package com.ndgndg91.ordersimulator

import com.ndgndg91.ordersimulator.client.JavaEngineClient
import com.ndgndg91.ordersimulator.client.KotlinEngineClient
import com.ndgndg91.ordersimulator.client.request.AddOrderRequest
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.context.annotation.Bean
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.random.nextInt

@EnableFeignClients
@SpringBootApplication
open class OrderSimulatorApplication {

    private val log = LoggerFactory.getLogger("OrderSimulatorApplication")

    @Autowired
    private lateinit var kotlinEngineClient: KotlinEngineClient

    @Autowired
    private lateinit var javaEngineClient: JavaEngineClient

    @Bean
    open fun runner(): CommandLineRunner {
        val executor = Executors.newFixedThreadPool(20)
        return CommandLineRunner {
            val maxShare = 5000
            val minShare = 50

            val maxPrice = 100_000
            val minPrice = 90_000

            while (true) {
                val orderType = if (Random.nextInt(2) == 0) "ASK" else "BID"
                val priceType = if (Random.nextInt(20) % 19 == 0) "MARKET" else "LIMIT"
                val shares = Random.nextInt(minShare..maxShare).toString()
                val price = Random.nextInt(minPrice..maxPrice).toString()
                val symbol = symbol(Random.nextInt(8))

                val addOrderRequest = AddOrderRequest(orderType, symbol, shares, priceType, price)

//                javaEngineClient.addOrder(addOrderRequest)
                log.info("new request start")
                val future = executor.submit { kotlinEngineClient.addOrder(addOrderRequest) }
                val get = future.get()
                log.info("$get")
                Thread.sleep(25)
            }
        }
    }

    private fun symbol(v: Int): String = when (v) {
        0 -> "MSFT"
        1 -> "GOOG"
        2 -> "AMZN"
        3 -> "TSLA"
        4 -> "TSM"
        5 -> "FB"
        6 -> "KO"
        else -> "AAPL"
    }

}

fun main(args: Array<String>) {
    runApplication<OrderSimulatorApplication>(*args)
}