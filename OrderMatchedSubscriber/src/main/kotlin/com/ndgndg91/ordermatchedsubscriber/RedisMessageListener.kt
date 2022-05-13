package com.ndgndg91.ordermatchedsubscriber

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.ndgndg91.ordermatchedsubscriber.order.ChannelResult
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.Message
import org.springframework.data.redis.connection.MessageListener
import org.springframework.messaging.simp.SimpMessageSendingOperations
import org.springframework.stereotype.Component

@Component
class RedisMessageListener(
    private val messagingTemplate: SimpMessageSendingOperations,
) : MessageListener {
    companion object {
        private val log = LoggerFactory.getLogger(RedisMessageListener::class.java)
        private val om = jacksonObjectMapper()
            .enable(DeserializationFeature.READ_ENUMS_USING_TO_STRING)
    }

    override fun onMessage(message: Message, pattern: ByteArray?) {
        val string = String(message.body)
        val channel = String(message.channel)
        log.info("========")
        log.info(string)
        log.info(channel)
        val body: ChannelResult = om.readValue(string, ChannelResult::class.java)
        log.info("$body")
        log.info("========")
        messagingTemplate.convertAndSend("/topic/message", string)
    }
}