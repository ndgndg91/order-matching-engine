package com.ndgndg91.ordermatchedsse

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext

@Configuration
class RedisConfiguration {

    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    @Bean
    fun reactiveRedisTemplate(): ReactiveRedisTemplate<Any, Any> {
        val om = ObjectMapper()
        val jackson2JsonRedisSerializer = Jackson2JsonRedisSerializer(Any::class.java)
        jackson2JsonRedisSerializer.setObjectMapper(om)
        val redisSerializationContext = RedisSerializationContext.newSerializationContext<Any, Any>()
            .key(jackson2JsonRedisSerializer)
            .value(jackson2JsonRedisSerializer)
            .hashKey(jackson2JsonRedisSerializer)
            .hashValue(jackson2JsonRedisSerializer)
            .build()

        return ReactiveRedisTemplate(reactiveRedisConnectionFactory(), redisSerializationContext)
    }
}