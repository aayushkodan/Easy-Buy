package com.aayush.easybuyinventoryservice.dto.response;

import java.time.Instant;
import java.util.UUID;

public record InventoryResponse(

        UUID id,

        UUID productId,

        String sku,

        String productName,

        String warehouseLocation,

        Integer availableQuantity,

        Integer reservedQuantity,

        Integer totalQuantity,

        Integer reorderLevel,

        Boolean lowStock,

        Boolean active,

        Instant createdAt,

        Instant updatedAt

) {
}
