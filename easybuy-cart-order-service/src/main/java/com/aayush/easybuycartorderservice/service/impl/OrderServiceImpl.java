package com.aayush.easybuycartorderservice.service.impl;

import com.aayush.easybuycartorderservice.dto.request.CheckoutRequest;
import com.aayush.easybuycartorderservice.dto.request.ReleaseStockRequest;
import com.aayush.easybuycartorderservice.dto.request.ReserveStockRequest;
import com.aayush.easybuycartorderservice.dto.response.InventorySummaryResponse;
import com.aayush.easybuycartorderservice.dto.response.OrderResponse;
import com.aayush.easybuycartorderservice.entity.*;
import com.aayush.easybuycartorderservice.exception.EasybuyException;
import com.aayush.easybuycartorderservice.exception.ErrorCode;
import com.aayush.easybuycartorderservice.external.InventoryClient;
import com.aayush.easybuycartorderservice.mapper.OrderMapper;
import com.aayush.easybuycartorderservice.repository.CartRepository;
import com.aayush.easybuycartorderservice.repository.OrderRepository;
import com.aayush.easybuycartorderservice.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {
    private final CartRepository cartRepository;
    private final InventoryClient inventoryClient;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    @Override
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {
        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> new EasybuyException(ErrorCode.CART_NOT_FOUND, "Cart not found for user: " + userId));

        if (cart.getItems().isEmpty()) {
            throw new EasybuyException(ErrorCode.CART_EMPTY, "Cart is empty for user: " + userId);
        }

        List<InventorySummaryResponse> reservedInventory = new ArrayList<>();

        for (CartItem cartItem : cart.getItems()) {
            reservedInventory.add(inventoryClient.reserveStock(cartItem.getProductId(), new ReserveStockRequest(cartItem.getQuantity())));
        }

        Order order = orderRepository.save(buildOrderFromCart(cart, request));

        cart.setStatus(CartStatus.CHECKED_OUT);
        cart.setCheckedOutAt(Instant.now());
        cart.getItems().clear();
        cartRepository.save(cart);


        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrderById(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EasybuyException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));
        return orderMapper.toResponse(order);
    }

    @Override
    public OrderResponse getOrderByNumber(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new EasybuyException(ErrorCode.ORDER_NOT_FOUND, "Order not found with number: " + orderNumber));
        return orderMapper.toResponse(order);
    }

    @Override
    public List<OrderResponse> getOrdersByUserId(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse cancelOrder(UUID orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EasybuyException(ErrorCode.ORDER_NOT_FOUND, "Order not found with ID: " + orderId));

        if (order.getStatus() == OrderStatus.CANCELLED) {
            return orderMapper.toResponse(order);
        }

        for (OrderItem item : order.getItems()) {
            inventoryClient.releaseStock(item.getProductId(), new ReleaseStockRequest(item.getQuantity()));
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(Instant.now());

        return orderMapper.toResponse(orderRepository.save(order));
    }

    @Override
    public void releaseReservedStock(UUID productId, Integer quantity) {
        inventoryClient.releaseStock(productId, new ReleaseStockRequest(quantity));
    }

    @Override
    public void updatePaymentStatus(UUID orderId, String paymentStatus) {
//will do in future
    }

    private Order buildOrderFromCart(Cart cart, CheckoutRequest request) {
        Order order = new Order();
        order.setOrderNumber(UUID.randomUUID().toString());
        order.setUserId(cart.getUserId());
        order.setBillingName(request.billingName().trim());
        order.setBillingPhone(request.billingPhone().trim());
        order.setExtraInformation(request.extraInformation().trim());
        order.setShippingAddress(request.shippingAddress().trim());
        order.setPaymentMethod(request.paymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.CONFIRMED);
        order.setItems(new ArrayList<>());

        BigDecimal total = BigDecimal.ZERO;

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductTitle(cartItem.getProductTitle());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setDiscountPercent(cartItem.getDiscountPercent());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setLineTotal(cartItem.getUnitPrice().multiply(BigDecimal.valueOf(cartItem.getQuantity())).setScale(2, RoundingMode.HALF_UP));
            order.getItems().add(orderItem);
            total = total.add(orderItem.getLineTotal());
        }
        order.setTotalAmount(total.setScale(2, RoundingMode.HALF_UP));
        return order;
    }
}
