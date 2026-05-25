package com.aayush.easybuyapigateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RouteConfig {

    @Bean
    public RouteLocator route(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("product-service", route ->
                        route.path("/products/**")
                                .filters(filter ->
                                                filter.rewritePath(
                                                        "/products(?<segment>/?.*)",
                                                        "/api/v1/products${segment}"
                                                )
                                        )
                                .uri("lb://EASYBUY-PRODUCT-SERVICE")
                )
                .route("order-service", route ->
                        route.path("/orders/**")
                                .filters(filter ->
                                        filter.rewritePath("/orders/?(?<segment>.*)", "/api/v1/orders/${segment}")
                                        )
                                .uri("lb://EASYBUY-ORDER-SERVICE")
                )
                .build();
    }
}
