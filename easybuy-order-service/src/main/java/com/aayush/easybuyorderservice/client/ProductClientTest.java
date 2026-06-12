package com.aayush.easybuyorderservice.client;

import com.aayush.easybuyorderservice.dto.response.PagedResponse;
import com.aayush.easybuyorderservice.dto.response.ProductResponse;
import com.aayush.easybuyorderservice.fallbacks.ProductClientFallback;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EASYBUY-PRODUCT-SERVICE", fallback = ProductClientFallback.class)
public interface ProductClientTest {

    @GetMapping("/api/v1/products")
    PagedResponse<ProductResponse> getAllProducts();
}
