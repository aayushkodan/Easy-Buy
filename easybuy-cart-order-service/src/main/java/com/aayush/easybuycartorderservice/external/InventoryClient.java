package com.aayush.easybuycartorderservice.external;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.aayush.easybuycartorderservice.dto.response.*;
import com.aayush.easybuycartorderservice.dto.request.*;
import java.util.UUID;

@FeignClient(name = "EASYBUY-INVENTORY-SERVICE")
public interface InventoryClient {

    @PostMapping("/api/v1/inventory/product/{productId}/reserve")
    InventorySummaryResponse reserveStock(
            @PathVariable UUID productId,
            @RequestBody ReserveStockRequest request
    );

    @PostMapping("/api/v1/inventory/product/{productId}/release")
    InventorySummaryResponse releaseStock(
            @PathVariable UUID productId,
            @RequestBody ReleaseStockRequest request
    );
}