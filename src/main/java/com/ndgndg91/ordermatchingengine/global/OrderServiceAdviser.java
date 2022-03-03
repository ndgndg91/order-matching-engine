package com.ndgndg91.ordermatchingengine.global;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class OrderServiceAdviser {

    @ExceptionHandler(OrderServiceException.class)
    public ResponseEntity<ApiError> orderServiceException(OrderServiceException e) {
        return ResponseEntity
                .status(e.getStatus())
                .body(new ApiError(e.getStatus(), e.getMessage(), e.getPath(), e.getTimestamp()));
    }
}
