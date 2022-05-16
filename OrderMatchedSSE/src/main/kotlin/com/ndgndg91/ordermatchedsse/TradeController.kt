package com.ndgndg91.ordermatchedsse

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class TradeController(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {
    companion object {
        private val log = LoggerFactory.getLogger(TradeController::class.java)
        private val om = jacksonObjectMapper()
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    }

    @GetMapping("/trade/{symbol}")
    fun listen(@PathVariable("symbol") symbol: String): Flux<ServerSentEvent<ChannelResult>> {
        return this.reactiveRedisTemplate
            .listenToChannel("${symbol}:matched-channel")
            .also { log.info("$it") }
            .map { om.readValue(it.message.toString(), ChannelResult::class.java) }
            .map { ServerSentEvent.builder(it).build() }
    }
}