package com.aayush.easybuyinventoryservice.dto.response;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductSummaryResponse(
        UUID id,
        String title,
        BigDecimal price
) {}