package com.ndgndg91.ordermatchedsse.controller

import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.service.TradeService
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
class TradeController(private val tradeService: TradeService) {

    @GetMapping("/trade/{symbol}")
    fun listen(@PathVariable("symbol") symbol: String): Flux<ServerSentEvent<ChannelResult>> {
        return this.tradeService.listen(symbol)
            .map { ServerSentEvent.builder(it).build() }
    }
}