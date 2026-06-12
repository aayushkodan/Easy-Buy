package com.aayush.easybuyorderservice.fallbacks;

import com.aayush.easybuyorderservice.client.ProductClientTest;
import com.aayush.easybuyorderservice.dto.response.PagedResponse;
import com.aayush.easybuyorderservice.dto.response.ProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ProductClientFallback implements ProductClientTest {
    @Override
    public PagedResponse<ProductResponse> getAllProducts() {
        return null;
    }
}
