package com.ndgndg91.ordermatchedsubscriber.order

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Controller

@Controller
class StompController {
    companion object {
        private val log = LoggerFactory.getLogger(StompController::class.java)
    }

    @MessageMapping("/TTT") // client 는 해당 endpoint 로 요청을 보내고
    @SendTo("/topic/message") // client 는 해당 endpoint 를 구독한다.
    fun ttt(message: String): String {
        log.info(message)
        return message
    }
}