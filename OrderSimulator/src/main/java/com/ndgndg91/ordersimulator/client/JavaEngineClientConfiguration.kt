package com.ndgndg91.ordersimulator.client

import feign.RequestInterceptor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
open class JavaEngineClientConfiguration {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(JavaEngineClientConfiguration::class.java)
    }
    @Bean
    open fun javaRequestInterceptor(): RequestInterceptor {
        return RequestInterceptor {
            log.info("in java engine client feign request interceptor!")
        }
    }

    @Bean
    open fun javaEngineClientLogger(): feign.Logger.Level {
        return feign.Logger.Level.FULL
    }
}