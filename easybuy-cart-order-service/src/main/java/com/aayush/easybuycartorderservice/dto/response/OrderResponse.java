package com.aayush.easybuycartorderservice.dto.response;

import com.aayush.easybuycartorderservice.entity.OrderStatus;
import com.aayush.easybuycartorderservice.entity.PaymentMethod;
import com.aayush.easybuycartorderservice.entity.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(

        UUID id,

        UUID userId,

        List<OrderItemResponse> items,

        BigDecimal totalAmount,

        PaymentMethod paymentMethod,

        PaymentStatus paymentStatus,

        OrderStatus orderStatus,

        Instant createdAt,

        Instant updatedAt

) {
}