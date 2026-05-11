package com.aayush.productservice.dto.response;

import com.aayush.productservice.entity.Category;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ProductResponse(

        UUID id,

        String title,

        String shortDesc,

        String longDesc,

        BigDecimal price,

        Integer discount,

        Boolean live,

        List<String> productImages,

        CategoryResponse category,

        Instant createdAt,

        Instant updatedAt

) {
}