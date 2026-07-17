package com.aayush.easybuyapigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;

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
                                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                                .circuitBreaker(c -> c.setName("product-service-circuit-breaker")
                                                        .setFallbackUri("/product-fallback")
                                                )

                                )
                                .uri("lb://EASYBUY-PRODUCT-SERVICE")
                )
                .route("order-service", route ->
                        route.path("/orders/**")
                                .filters(filter ->
                                        filter.rewritePath("/orders/?(?<segment>.*)", "/api/v1/orders/${segment}")
                                                .retry(retryConfig ->
                                                        retryConfig.setRetries(3)
                                                                .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(10000), 2, true)
                                                )
                                )
                                .uri("lb://EASYBUY-ORDER-SERVICE")
                )
                .route("user-service", route ->
                        route.path("/users/**")
                                .filters(filter ->
                                        filter.rewritePath("/users/?(?<segment>.*)", "/api/v1/users/${segment}")
                                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                                .retry(retryConfig ->
                                                        retryConfig.setRetries(3)
                                                                .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(10000), 2, true)
                                                )
                                )
                                .uri("lb://EASYBUY-USER-SERVICE")
                        )
                .route("inventory-service", route ->
                        route.path("/inventories/**")
                                .filters(filter ->
                                        filter.rewritePath("/inventories/?(?<segment>.*)", "/api/v1/inventories/${segment}")
                                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                                .retry(retryConfig ->
                                                        retryConfig.setRetries(3)
                                                                .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(10000), 2, true)
                                                )
                                )
                                .uri("lb://EASYBUY-INVENTORY-SERVICE")
                )
                .route("cart-service", route ->
                        route.path("/carts/**")
                                .filters(filter ->
                                        filter.rewritePath("/carts/?(?<segment>.*)", "/api/v1/carts/${segment}")
                                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                                .retry(retryConfig ->
                                                        retryConfig.setRetries(3)
                                                                .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(10000), 2, true)
                                                )
                                )
                                .uri("lb://EASYBUY-INVENTORY-SERVICE")
                )
                .route("order-service", route ->
                        route.path("/orders/**")
                                .filters(filter ->
                                        filter.rewritePath("/orders/?(?<segment>.*)", "/api/v1/orders/${segment}")
                                                .requestRateLimiter(c -> c.setRateLimiter(redisRateLimiter())
                                                        .setKeyResolver(keyResolver())
                                                )
                                                .retry(retryConfig ->
                                                        retryConfig.setRetries(3)
                                                                .setBackoff(Duration.ofMillis(1000), Duration.ofMillis(10000), 2, true)
                                                )
                                )
                                .uri("lb://EASYBUY-INVENTORY-SERVICE")
                )
                .build();
    }

    @Bean
    public KeyResolver keyResolver() {
        return exchange -> Mono.just(Objects.requireNonNull(exchange.getRequest().getHeaders().getFirst("user"))).defaultIfEmpty("anonymous");
    }

    @Bean
    public RedisRateLimiter redisRateLimiter() {
        return new RedisRateLimiter(1, 1, 1);
    }
}
