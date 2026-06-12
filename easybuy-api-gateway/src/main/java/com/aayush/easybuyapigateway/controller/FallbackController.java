package com.aayush.easybuyapigateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class FallbackController {

    @GetMapping("/product-fallback")
    public Mono<String> productCircuitBreakerFallback() {
        return Mono.just("Product Service is down");
    }
}
