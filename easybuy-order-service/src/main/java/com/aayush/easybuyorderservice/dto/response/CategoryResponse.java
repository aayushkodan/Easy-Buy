package com.aayush.easybuyorderservice.dto.response;

import java.time.Instant;

public record CategoryResponse(

        Long id,

        String title,

        Instant createdAt,

        Instant updatedAt

) {
}

