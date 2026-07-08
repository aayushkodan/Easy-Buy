package com.aayush.easybuycartorderservice.dto.response;

import java.util.UUID;

public record InventorySummaryResponse(

        UUID id,

        UUID productId,

        String sku,

        Integer availableQuantity,

        Integer reservedQuantity,

        Boolean lowStock

) {
}