package com.aayush.productservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateCategoryRequest(

        @NotBlank(message = "Category title is required")
        @Size(min = 2, max = 50, message = "Category title must be between 2 and 50 characters")
        String title

) {
}