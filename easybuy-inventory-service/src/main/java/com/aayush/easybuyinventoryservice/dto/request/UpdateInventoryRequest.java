package com.aayush.easybuyinventoryservice.dto.request;

import jakarta.validation.constraints.Min;

public record UpdateInventoryRequest(

        String productName,

        String warehouseLocation,

        @Min(0)
        Integer reorderLevel,

        Boolean active

) {
}