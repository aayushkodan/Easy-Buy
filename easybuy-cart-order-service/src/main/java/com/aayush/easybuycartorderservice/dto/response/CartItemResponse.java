package com.aayush.easybuycartorderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(

        UUID productId,

        String productTitle,

        BigDecimal unitPrice,

        Integer quantity,

        Integer discountPercent,

        BigDecimal lineTotal

) {
}