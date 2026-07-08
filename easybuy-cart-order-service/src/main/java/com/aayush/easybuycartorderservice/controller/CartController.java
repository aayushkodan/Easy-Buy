package com.aayush.easybuycartorderservice.controller;

import com.aayush.easybuycartorderservice.dto.request.AddCartItemRequest;
import com.aayush.easybuycartorderservice.dto.request.UpdateCartItemRequest;
import com.aayush.easybuycartorderservice.dto.response.CartResponse;
import com.aayush.easybuycartorderservice.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@Validated
@RequestMapping("/api/v1/carts")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping("/{userId}")
    public CartResponse getCart(@PathVariable UUID userId) {
        return cartService.getCart(userId);
    }

    @PostMapping("/{userId}/items")
    public ResponseEntity<CartResponse> addItem(@PathVariable UUID userId, @Valid @RequestBody AddCartItemRequest request) {
        return ResponseEntity.ok(cartService.addItem(userId, request));
    }

    @PutMapping("/{userId}/items/{productId}")
    public CartResponse updateItem(@PathVariable UUID userId, @PathVariable UUID productId, @Valid @RequestBody UpdateCartItemRequest request) {
        return cartService.updateItem(userId, productId, request);
    }

    @DeleteMapping("/{userId}/items/{productId}")
    public CartResponse removeItem(@PathVariable UUID userId, @PathVariable UUID productId) {
        return cartService.removeItem(userId, productId);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
        return ResponseEntity.noContent().build();
    }
}
