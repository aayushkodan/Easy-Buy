package com.aayush.easybuycartorderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(

        UUID id,

        UUID productId,

        String productTitle,

        BigDecimal unitPrice,

        Integer discountPercent,

        Integer quantity,

        BigDecimal lineTotal

) {
}