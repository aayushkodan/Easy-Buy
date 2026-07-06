package com.aayush.easybuycartorderservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(

        UUID id,

        String title,

        BigDecimal price,

        Integer discount,

        Boolean live

) {
}