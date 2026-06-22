package com.aayush.easybuyinventoryservice.external;

import com.aayush.easybuyinventoryservice.dto.response.ProductSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "EASYBUY-PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping("/api/v1/products/{id}")
    ProductSummaryResponse getProductById(@PathVariable("id") UUID id);
}
