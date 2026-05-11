package com.aayush.productservice.dto.response;

import java.time.Instant;

public record ReviewResponse(

        Long id,

        String title,

        String comment,

        Integer rating,

        Instant createdAt,

        Instant updatedAt

) {
}