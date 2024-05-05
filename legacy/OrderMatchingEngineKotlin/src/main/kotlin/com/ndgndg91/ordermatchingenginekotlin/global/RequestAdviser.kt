package com.ndgndg91.ordermatchingenginekotlin.global

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime
import javax.validation.ConstraintViolationException

@RestControllerAdvice
class RequestAdviser {
    val log: Logger = LoggerFactory.getLogger(RequestAdviser::class.java)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun notValidException(e: MethodArgumentNotValidException): ResponseEntity<ApiError> {
        val message = e.bindingResult.fieldErrors
            .asSequence()
            .map { it.defaultMessage }
            .joinToString("\t")

        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(HttpStatus.BAD_REQUEST.value(), message, null, LocalDateTime.now()))
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun notReadableException(e: HttpMessageNotReadableException): ResponseEntity<ApiError> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(HttpStatus.BAD_REQUEST.value(), e.message!!, null, LocalDateTime.now()))
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(e: ConstraintViolationException): ResponseEntity<ApiError> {
        val message = e.constraintViolations
            .asSequence()
            .map { it.messageTemplate }
            .joinToString("\t")

        log.info(message)
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(ApiError(HttpStatus.BAD_REQUEST.value(), message, null, LocalDateTime.now()))
    }



}