package com.ndgndg91.ordermatchingenginekotlin.global

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class OrderServiceAdviser {

    @ExceptionHandler(OrderServiceException::class)
    fun orderServiceException(e: OrderServiceException): ResponseEntity<ApiError> {
       return ResponseEntity.status(e.status)
           .body(e.message?.let { ApiError(e.status, it, e.path, e.timestamp) });
    }
}