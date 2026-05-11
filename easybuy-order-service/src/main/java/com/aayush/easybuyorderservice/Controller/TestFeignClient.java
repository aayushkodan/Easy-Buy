package com.aayush.easybuyorderservice.Controller;

import com.aayush.easybuyorderservice.client.ProductClientTest;
import com.aayush.easybuyorderservice.dto.response.PagedResponse;
import com.aayush.easybuyorderservice.dto.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor
public class TestFeignClient {

    private final ProductClientTest productClientTest;

    @GetMapping
    public ResponseEntity<PagedResponse<ProductResponse>> testFeignClient(){
        return ResponseEntity.ok(productClientTest.getAllProducts());
    }
}
