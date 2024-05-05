package com.ndgndg91.ordermatchedsse.repository

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.controller.TradeController
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux

@Repository
class TradeRepository(private val reactiveRedisTemplate: ReactiveRedisTemplate<String, String>) {
    companion object {
        private val log = LoggerFactory.getLogger(TradeController::class.java)
        private val om = jacksonObjectMapper()
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    }

    fun listen(symbol: String): Flux<ChannelResult> {
        return reactiveRedisTemplate.listenToChannel("${symbol}:matched-channel")
            .also { log.info("$it") }
            .map { om.readValue(it.message.toString(), ChannelResult::class.java) }
    }
}