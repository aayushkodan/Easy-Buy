package com.aayush.easybuycommon.events;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderEvent(

        UUID orderId,

        UUID userId,

        String status,

        String message,

        BigDecimal totalAmount

) {
}
