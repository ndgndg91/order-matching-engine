package com.ndgndg91.ordersimulator.client

import feign.Response
import feign.codec.Decoder
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.support.ResponseEntityDecoder
import java.lang.reflect.Type

class KotlinEngineClientDecoder(decoder: Decoder): ResponseEntityDecoder(decoder) {
    companion object {
        private val log: Logger = LoggerFactory.getLogger(KotlinEngineClientDecoder::class.java)
    }

    override fun decode(response: Response?, type: Type?): Any {
        val headers = response?.headers()
        log.info("$headers")
        return super.decode(response, type)
    }
}