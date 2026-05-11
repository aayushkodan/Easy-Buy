package com.aayush.productservice.dto.response;

import java.time.Instant;

public record CategoryResponse(

        Long id,

        String title,

        Instant createdAt,

        Instant updatedAt

) {
}
