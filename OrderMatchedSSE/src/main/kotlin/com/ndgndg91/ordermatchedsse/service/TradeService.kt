package com.ndgndg91.ordermatchedsse.service

import com.ndgndg91.ordermatchedsse.ChannelResult
import com.ndgndg91.ordermatchedsse.repository.TradeRepository
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class TradeService(private val tradeRepository: TradeRepository) {

    fun listen(symbol: String): Flux<ChannelResult> {
        return tradeRepository.listen(symbol)
    }
}