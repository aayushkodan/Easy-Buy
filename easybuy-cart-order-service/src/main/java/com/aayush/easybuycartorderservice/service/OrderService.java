package com.aayush.easybuycartorderservice.service;

import com.aayush.easybuycartorderservice.dto.request.CheckoutRequest;
import com.aayush.easybuycartorderservice.dto.response.OrderResponse;

import java.util.List;
import java.util.UUID;


public interface OrderService {

    OrderResponse checkout(UUID userId, CheckoutRequest request);

    OrderResponse getOrderById(UUID orderId);

    OrderResponse getOrderByNumber(String orderNumber);

    List<OrderResponse> getOrdersByUserId(UUID userId);

    OrderResponse cancelOrder(UUID orderId);

    void releaseReservedStock(UUID productId, Integer quantity);

    void updatePaymentStatus(UUID orderId, String paymentStatus);
}