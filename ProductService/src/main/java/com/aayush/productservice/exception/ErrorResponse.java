package com.aayush.productservice.exception;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;
import java.util.Map;

@Builder
public record ErrorResponse(

        String code,

        String message,

        int status,

        Instant timestamp,

        String error,

        String path,

        Map<String, String> validationErrors

) {
}
