package com.aayush.productservice.dto.request;

import jakarta.validation.constraints.Size;

public record UpdateCategoryRequest(

        @Size(min = 2, max = 50, message = "Category title must be between 2 and 50 characters")
        String title

) {
}