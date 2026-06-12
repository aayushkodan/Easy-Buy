package com.aayush.easybuyuserservice.exception;

import lombok.Builder;

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
