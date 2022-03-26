package com.ndgndg91.ordermatchingenginekotlin.order.repository

import com.ndgndg91.ordermatchingenginekotlin.order.MatchResult
import com.ndgndg91.ordermatchingenginekotlin.order.Symbol
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class MatchOrderRepository(private val redisTemplate: RedisTemplate<String, String>) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(MatchOrderRepository::class.java)
    }
    fun save(symbol: Symbol, matchResult: List<MatchResult>) {
        redisTemplate.opsForList().rightPush("${symbol}:matchedOrder", "$matchResult")
    }

    fun findAllBySymbol(symbol: String): List<String>? {
        return redisTemplate.opsForList().range("${symbol}:matchedOrder", 0, -1)
    }
}