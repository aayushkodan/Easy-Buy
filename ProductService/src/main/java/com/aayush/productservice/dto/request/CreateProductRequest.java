package com.aayush.productservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.List;

public record CreateProductRequest(

        @NotBlank(message = "Title is required")
        String title,

        @NotBlank(message = "Short description is required")
        String shortDesc,

        @NotBlank(message = "Long description is required")
        String longDesc,

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be positive")
        BigDecimal price,

        @Min(value = 0, message = "Discount cannot be negative")
        @Max(value = 100, message = "Discount cannot exceed 100")
        Integer discount,

        Boolean live,

        @NotNull(message = "Category id is required")
        Long categoryId

) {
}
