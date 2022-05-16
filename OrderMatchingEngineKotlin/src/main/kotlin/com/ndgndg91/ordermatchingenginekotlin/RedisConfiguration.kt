package com.ndgndg91.ordermatchingenginekotlin

import com.fasterxml.jackson.databind.ObjectMapper
import com.ndgndg91.ordermatchingenginekotlin.order.Symbol
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.listener.ChannelTopic
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer


@Configuration
class RedisConfiguration {

    @Bean
    fun lettuceConnectionFactory(): RedisConnectionFactory {
        return LettuceConnectionFactory()
    }

    @Bean
    fun redisTemplate(): RedisTemplate<String, String> {
        val om = ObjectMapper()
        // redis serialize
        val jackson2JsonRedisSerializer: Jackson2JsonRedisSerializer<*> = Jackson2JsonRedisSerializer(String::class.java)
        jackson2JsonRedisSerializer.setObjectMapper(om)
        val template = StringRedisTemplate(lettuceConnectionFactory())
        template.keySerializer = jackson2JsonRedisSerializer
        template.valueSerializer = jackson2JsonRedisSerializer
        template.hashKeySerializer = jackson2JsonRedisSerializer
        template.hashValueSerializer = jackson2JsonRedisSerializer
        template.afterPropertiesSet()
        return template
    }

    @Bean
    fun channels(): Map<Symbol, ChannelTopic> {
        return Symbol.values().associateWith { ChannelTopic("${it.name}:matched-channel") }
    }
}