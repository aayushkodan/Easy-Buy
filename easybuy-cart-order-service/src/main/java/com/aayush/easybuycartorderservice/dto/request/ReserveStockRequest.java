package com.aayush.easybuycartorderservice.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ReserveStockRequest(

        @NotNull
        @Min(1)
        Integer quantity

) {
}
