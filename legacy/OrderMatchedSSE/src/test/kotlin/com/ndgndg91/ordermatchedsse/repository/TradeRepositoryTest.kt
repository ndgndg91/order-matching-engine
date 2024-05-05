package com.ndgndg91.ordermatchedsse.repository

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.Symbol
import com.ndgndg91.ordermatchedsse.containers.TradeRedisContainer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.redis.core.ReactiveRedisTemplate
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
internal class TradeRepositoryTest: TradeRedisContainer {

    @Autowired
    private lateinit var tradeRepository: TradeRepository

    @Autowired
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, String>

    // TODO: make it pass
    @Test
    fun test_listen() {
        // given
        val channelResult = ChannelResult(
            Symbol.AAPL,
            BigDecimal.ZERO,
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigInteger()
        )
        val om = jacksonObjectMapper().enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
//        redisTemplate.convertAndSend("AAPL:matchedOrder", om.writeValueAsString(channelResult))

        // when - then
        StepVerifier.create(tradeRepository.listen(channelResult.symbol.name))
            .expectNextMatches { it == channelResult }
            .expectComplete()
            .verify()


    }


}