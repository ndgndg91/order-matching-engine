package com.ndgndg91.ordersimulator.client

import feign.RequestInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean

open class KotlinEngineClientConfiguration {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(KotlinEngineClientConfiguration::class.java)
    }
    @Bean
    open fun kotlinRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            log.info("in kotlin engine client feign request interceptor!")
        }
    }

    @Bean
    open fun kotlinEngineClientLogger(): feign.Logger.Level {
        return feign.Logger.Level.FULL
    }
}