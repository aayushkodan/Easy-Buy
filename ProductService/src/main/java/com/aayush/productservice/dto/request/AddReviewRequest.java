package com.aayush.productservice.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AddReviewRequest(

        @NotBlank(message = "Review title is required")
        String title,

        @NotBlank(message = "Comment is required")
        String comment,

        @NotNull(message = "Rating is required")
        @Min(value = 1, message = "Minimum rating is 1")
        @Max(value = 5, message = "Maximum rating is 5")
        Integer rating

) {
}