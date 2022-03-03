package com.ndgndg91.ordermatchingengine.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@ToString
@RequiredArgsConstructor
public final class ApiError {
    private final int status;
    private final String message;
    private final String path;
    private final LocalDateTime timestamp;
}
