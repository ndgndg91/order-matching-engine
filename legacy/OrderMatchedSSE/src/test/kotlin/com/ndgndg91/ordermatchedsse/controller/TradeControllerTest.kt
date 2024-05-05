package com.ndgndg91.ordermatchedsse.controller

import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.Symbol
import com.ndgndg91.ordermatchedsse.service.TradeService
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.core.publisher.Flux
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.ZoneOffset

@WebFluxTest(controllers = [TradeController::class])
internal class TradeControllerTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @MockBean
    private lateinit var tradeService: TradeService

    @Test
    fun test_listen() {
        // given
        val symbol = "AAPL"
        val channelResult = ChannelResult(
            Symbol.AAPL,
            BigDecimal.ZERO,
            LocalDateTime.now().toEpochSecond(ZoneOffset.UTC).toBigInteger()
        )
        given(tradeService.listen(symbol)).willReturn(Flux.just(channelResult))

        // when - then
        webTestClient.get()
            .uri("/trade/{symbol}", symbol)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectHeader()
            .contentType("text/event-stream;charset=UTF-8")
            .expectBody()
            .consumeWith {
                val body =  String(it.responseBody!!).trim()
                assertThat(body).isEqualTo("data:{\"symbol\":\"AAPL\",\"price\":0,\"timestamp\":${channelResult.timestamp}}")
            }
    }
}