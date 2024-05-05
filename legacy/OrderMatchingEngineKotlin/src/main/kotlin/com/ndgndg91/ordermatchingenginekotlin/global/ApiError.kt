package com.ndgndg91.ordermatchingenginekotlin.global

import java.time.LocalDateTime

data class ApiError(
    val status: Int,
    val message: String,
    val path: String?,
    val timestamp: LocalDateTime = LocalDateTime.now()
)
