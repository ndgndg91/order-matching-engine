package com.ndgndg91.ordermatchingengine.global;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Getter
@ToString
@RequiredArgsConstructor
public class ApiResponse<T> {
    private final T data;
}
