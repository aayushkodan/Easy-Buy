package com.aayush.productservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record UpdateProductRequest(

        String title,

        String shortDesc,

        String longDesc,

        @Positive(message = "Price must be positive")
        BigDecimal price,

        @Min(value = 0, message = "Discount cannot be negative")
        @Max(value = 100, message = "Discount cannot exceed 100")
        Integer discount,

        Boolean live,

        List<String> productImages,

        Long categoryId

) {
}