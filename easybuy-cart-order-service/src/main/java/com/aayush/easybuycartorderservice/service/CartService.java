package com.aayush.easybuycartorderservice.service;

import com.aayush.easybuycartorderservice.dto.request.AddCartItemRequest;
import com.aayush.easybuycartorderservice.dto.request.UpdateCartItemRequest;
import com.aayush.easybuycartorderservice.dto.response.CartResponse;

import java.util.UUID;

public interface CartService {

    //get card by userid
    CartResponse getCart(UUID userId);

    // add item to cart
    CartResponse addItem(UUID userId, AddCartItemRequest request);

    //updating the quantity
    CartResponse updateItem(UUID userId, UUID productId, UpdateCartItemRequest request);

    //remove the item from cart
    CartResponse removeItem(UUID userId, UUID productId);

    //cart clear
    void clearCart(UUID userId);
}