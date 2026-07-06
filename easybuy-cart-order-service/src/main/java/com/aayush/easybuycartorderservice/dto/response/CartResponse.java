package com.aayush.easybuycartorderservice.dto.response;

import com.aayush.easybuycartorderservice.entity.CartStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CartResponse(

        UUID id,

        UUID userId,

        CartStatus status,

        List<CartItemResponse> items,

        Integer totalItems,

        BigDecimal totalAmount,

        Instant createdAt,

        Instant updatedAt

) {
}