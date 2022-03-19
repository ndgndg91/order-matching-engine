package com.ndgndg91.ordermatchingenginekotlin.order.repository

import com.ndgndg91.ordermatchingenginekotlin.order.MatchResult
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository

@Repository
class MatchOrderRepository(private val redisTemplate: RedisTemplate<String, String>) {
    fun save(matchResult: MatchResult) {
        redisTemplate.opsForList().rightPush("${matchResult.symbol}:matchedOrder", "")
    }
}