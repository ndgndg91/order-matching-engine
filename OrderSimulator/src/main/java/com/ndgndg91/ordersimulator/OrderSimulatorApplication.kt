package com.ndgndg91.ordersimulator

import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.concurrent.Executors
import kotlin.random.Random
import kotlin.random.nextInt

@SpringBootApplication
open class OrderSimulatorApplication {

    private val log = LoggerFactory.getLogger("OrderSimulatorApplication")

    @Bean
    open fun runner(): CommandLineRunner {
        return CommandLineRunner {
            val client = HttpClient.newBuilder()
                .executor(Executors.newFixedThreadPool(100))
                .build()

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
                val body = """{
                    "orderType": "$orderType",
                    "symbol": "$symbol",
                    "shares": $shares,
                    "priceType": "$priceType",
                    "price": $price}"""

                val post = HttpRequest.newBuilder(URI("http://localhost:9090/apis/orders"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .build()
                client.sendAsync(post, HttpResponse.BodyHandlers.ofString())
                    .thenApply { res ->
                        log.info("headers : {}", res.headers())
                        log.info("status code: {}", res.statusCode())
                        res
                    }.thenApply { it.body() }
                    .thenAccept(log::info)
                log.info("new request start")
                Thread.sleep(200L)
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