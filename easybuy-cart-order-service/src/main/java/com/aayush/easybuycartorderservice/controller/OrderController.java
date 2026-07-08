package com.aayush.easybuycartorderservice.controller;

import com.aayush.easybuycartorderservice.dto.request.CheckoutRequest;
import com.aayush.easybuycartorderservice.dto.response.OrderResponse;
import com.aayush.easybuycartorderservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<OrderResponse> checkout(@PathVariable UUID userId, @Valid @RequestBody CheckoutRequest request) {
        return ResponseEntity.ok(orderService.checkout(userId, request));
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrderById(@PathVariable UUID orderId) {
        return orderService.getOrderById(orderId);
    }

    @GetMapping("/number/{orderNumber}")
    public OrderResponse getOrderByNumber(@PathVariable String orderNumber) {
        return orderService.getOrderByNumber(orderNumber);
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getOrdersByUserId(@PathVariable UUID userId) {
        return orderService.getOrdersByUserId(userId);
    }

    @DeleteMapping("/{orderId}")
    public OrderResponse cancelOrder(@PathVariable UUID orderId) {
        return orderService.cancelOrder(orderId);
    }
}
