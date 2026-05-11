package com.aayush.easybuyorderservice.client;

import com.aayush.easybuyorderservice.dto.response.PagedResponse;
import com.aayush.easybuyorderservice.dto.response.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "EASYBUY-PRODUCT-SERVICE")
public interface ProductClientTest {

    @GetMapping("/api/v1/products")
    PagedResponse<ProductResponse> getAllProducts();
}
