package com.ndgndg91.ordermatchedsse.service

import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.Symbol
import com.ndgndg91.ordermatchedsse.repository.TradeRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.InjectMocks
import org.mockito.Mock
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.test.StepVerifier
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset


@ExtendWith(SpringExtension::class)
internal class TradeServiceTest {

    @InjectMocks
    private lateinit var tradeService: TradeService

    @Mock
    private lateinit var tradeRepository: TradeRepository

    @Test
    fun test_listen() {
        // given
        val symbol = Symbol.AAPL.name
        val channelResult = ChannelResult(
            Symbol.AAPL,
            BigDecimal.ZERO,
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigInteger()
        )
        given(tradeRepository.listen(symbol)).willReturn(Flux.just(channelResult))

        // when - then
        StepVerifier.create(tradeService.listen(symbol))
            .expectNextMatches{ it == channelResult }
            .expectComplete()
            .verify()
    }
}