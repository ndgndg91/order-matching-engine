package com.ndgndg91.ordermatchingenginekotlin.global

import java.time.LocalDateTime

data class ApiError(
    private val status: Int,
    private val message: String,
    private val path: String?,
    private val timestamp: LocalDateTime = LocalDateTime.now()
)
