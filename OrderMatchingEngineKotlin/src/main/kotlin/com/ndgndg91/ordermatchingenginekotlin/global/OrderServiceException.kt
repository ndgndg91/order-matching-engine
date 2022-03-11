package com.ndgndg91.ordermatchingenginekotlin.global

import java.time.LocalDateTime

class OrderServiceException(
    val path: String?, val status: Int, message: String) : RuntimeException(message) {
    val timestamp: LocalDateTime = LocalDateTime.now()
}