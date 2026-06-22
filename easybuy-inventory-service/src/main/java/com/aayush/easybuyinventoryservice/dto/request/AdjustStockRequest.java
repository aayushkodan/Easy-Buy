package com.aayush.easybuyinventoryservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record AdjustStockRequest(

        @NotNull
        Integer quantity,

        @NotBlank
        String reason

) {
}