package com.ndgndg91.ordermatchingengine.global;

import lombok.Getter;

import java.time.LocalDateTime;

public class OrderServiceException extends RuntimeException {
    @Getter
    private final int status;
    @Getter
    private final String path;
    @Getter
    private final LocalDateTime timestamp;

    public OrderServiceException(String path, int status, String message) {
        super(message);
        this.status = status;
        this.path = path;
        this.timestamp = LocalDateTime.now();
    }
}
