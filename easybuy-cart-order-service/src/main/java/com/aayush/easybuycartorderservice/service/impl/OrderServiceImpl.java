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
import com.aayush.easybuycartorderservice.messaging.publisher.OrderEventPublisher;
import com.aayush.easybuycartorderservice.repository.CartRepository;
import com.aayush.easybuycartorderservice.repository.OrderRepository;
import com.aayush.easybuycartorderservice.service.OrderService;
import com.aayush.easybuycommon.events.OrderEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
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
    private final OrderRepository orderRepository;
    private final InventoryClient inventoryClient;
    private final OrderMapper orderMapper;
    private final OrderEventPublisher orderEventPublisher;

    @Override
    public OrderResponse checkout(UUID userId, CheckoutRequest request) {

        log.info("Checkout started for user={}", userId);

        Cart cart = cartRepository.findByUserIdAndStatus(userId, CartStatus.ACTIVE)
                .orElseThrow(() -> {
                    log.warn("Active cart not found for user={}", userId);
                    return new EasybuyException(
                            ErrorCode.CART_NOT_FOUND,
                            "Cart not found for user: " + userId
                    );
                });

        if (cart.getItems().isEmpty()) {
            log.warn("Checkout failed because cart {} is empty", cart.getId());
            throw new EasybuyException(
                    ErrorCode.CART_EMPTY,
                    "Cart is empty."
            );
        }

        log.debug(
                "Cart {} loaded with {} items",
                cart.getId(),
                cart.getItems().size()
        );

        List<CartItem> reservedItems = new ArrayList<>();

        try {

            for (CartItem cartItem : cart.getItems()) {

                log.debug(
                        "Reserving {} units of product {}",
                        cartItem.getQuantity(),
                        cartItem.getProductId()
                );

                InventorySummaryResponse response =
                        inventoryClient.reserveStock(
                                cartItem.getProductId(),
                                new ReserveStockRequest(cartItem.getQuantity())
                        );

                log.debug(
                        "Inventory reserved successfully for product {}",
                        response.productId()
                );

                reservedItems.add(cartItem);
            }

            Order order = buildOrderFromCart(cart, request);

            order = orderRepository.save(order);

            log.info(
                    "Order {} created successfully",
                    order.getOrderNumber()
            );

            cart.setStatus(CartStatus.CHECKED_OUT);
            cart.setCheckedOutAt(java.time.Instant.now());
            cart.getItems().clear();

            cartRepository.save(cart);

            OrderEvent event =new OrderEvent(
                    order.getId(),
                    order.getUserId(),
                    order.getStatus().toString(),
                    "Order created successfully",
                    order.getTotalAmount()
            );

            orderEventPublisher.publishOrderEvent(event);

            log.info(
                    "Cart {} checked out successfully",
                    cart.getId()
            );

            log.info(
                    "Checkout completed successfully. orderId={}",
                    order.getId()
            );

            return orderMapper.toResponse(order);

        } catch (Exception ex) {

            log.error(
                    "Checkout failed for user {}. Rolling back reserved inventory.",
                    userId,
                    ex
            );

            rollbackReservedInventory(reservedItems);

            throw ex;
        }
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(UUID orderId) {

        log.debug("Fetching order {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order {} not found", orderId);
                    return new EasybuyException(
                            ErrorCode.ORDER_NOT_FOUND,
                            "Order not found with id: " + orderId
                    );
                });

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrderByNumber(String orderNumber) {

        log.debug("Fetching order {}", orderNumber);

        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> {
                    log.warn("Order {} not found", orderNumber);
                    return new EasybuyException(
                            ErrorCode.ORDER_NOT_FOUND,
                            "Order not found with number: " + orderNumber
                    );
                });

        return orderMapper.toResponse(order);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByUserId(UUID userId) {

        log.debug("Fetching orders for user {}", userId);

        List<Order> orders =
                orderRepository.findByUserIdOrderByCreatedAtDesc(userId);

        log.debug(
                "Found {} orders for user {}",
                orders.size(),
                userId
        );

        return orders.stream()
                .map(orderMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse cancelOrder(UUID orderId) {

        log.info("Cancelling order {}", orderId);

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> {
                    log.warn("Order {} not found", orderId);
                    return new EasybuyException(
                            ErrorCode.ORDER_NOT_FOUND,
                            "Order not found with id: " + orderId
                    );
                });

        if (order.getStatus() == OrderStatus.CANCELLED) {

            log.warn("Order {} is already cancelled", orderId);

            throw new EasybuyException(
                    ErrorCode.ORDER_ALREADY_CANCELLED,
                    "Order is already cancelled."
            );
        }

        if (order.getStatus() == OrderStatus.DELIVERED) {

            log.warn("Delivered order {} cannot be cancelled", orderId);

            throw new EasybuyException(
                    ErrorCode.ORDER_CANNOT_BE_CANCELLED,
                    "Delivered orders cannot be cancelled."
            );
        }

        for (OrderItem item : order.getItems()) {

            log.debug(
                    "Releasing {} units of product {}",
                    item.getQuantity(),
                    item.getProductId()
            );

            inventoryClient.releaseStock(
                    item.getProductId(),
                    new ReleaseStockRequest(item.getQuantity())
            );
        }

        order.setStatus(OrderStatus.CANCELLED);
        order.setCancelledAt(java.time.Instant.now());

        Order savedOrder = orderRepository.save(order);

        log.info(
                "Order {} cancelled successfully",
                savedOrder.getOrderNumber()
        );

        return orderMapper.toResponse(savedOrder);
    }

    @Override
    public void releaseReservedStock(UUID productId, Integer quantity) {

        log.info(
                "Releasing reserved stock. Product={}, Quantity={}",
                productId,
                quantity
        );

        inventoryClient.releaseStock(
                productId,
                new ReleaseStockRequest(quantity)
        );

        log.debug(
                "Reserved stock released successfully for product {}",
                productId
        );
    }

    @Override
    public void updatePaymentStatus(UUID orderId, String paymentStatus) {

        log.info(
                "Updating payment status of order {} to {}",
                orderId,
                paymentStatus
        );

        // Will be implemented after Payment Service
    }

    /**
     * Releases every successfully reserved inventory item.
     * Used when checkout fails after reserving stock.
     */
    private void rollbackReservedInventory(List<CartItem> reservedItems) {

        if (reservedItems.isEmpty()) {
            return;
        }

        log.warn(
                "Rolling back inventory reservation for {} items",
                reservedItems.size()
        );

        for (CartItem item : reservedItems) {

            try {

                inventoryClient.releaseStock(
                        item.getProductId(),
                        new ReleaseStockRequest(item.getQuantity())
                );

                log.debug(
                        "Released reserved stock for product {}",
                        item.getProductId()
                );

            } catch (Exception ex) {

                log.error(
                        "Failed to rollback reserved stock for product {}",
                        item.getProductId(),
                        ex
                );
            }
        }
    }

    private Order buildOrderFromCart(Cart cart, CheckoutRequest request) {

        log.debug("Building order from cart {}", cart.getId());

        Order order = new Order();

        order.setOrderNumber(generateOrderNumber());
        order.setUserId(cart.getUserId());

        order.setBillingName(request.billingName().trim());
        order.setBillingPhone(request.billingPhone().trim());
        order.setShippingAddress(request.shippingAddress().trim());

        order.setExtraInformation(
                request.extraInformation() == null
                        ? null
                        : request.extraInformation().trim()
        );

        order.setPaymentMethod(request.paymentMethod());
        order.setPaymentStatus(PaymentStatus.PENDING);
        order.setStatus(OrderStatus.CONFIRMED);

        order.setItems(new ArrayList<>());

        for (CartItem cartItem : cart.getItems()) {

            OrderItem orderItem = new OrderItem();

            orderItem.setOrder(order);
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductTitle(cartItem.getProductTitle());
            orderItem.setUnitPrice(cartItem.getUnitPrice());
            orderItem.setDiscountPercent(cartItem.getDiscountPercent());
            orderItem.setQuantity(cartItem.getQuantity());

            // CartItem already calculates lineTotal
            orderItem.setLineTotal(cartItem.getLineTotal());

            order.getItems().add(orderItem);
        }

        order.setTotalAmount(
                order.getItems()
                        .stream()
                        .map(OrderItem::getLineTotal)
                        .reduce(BigDecimal.ZERO, BigDecimal::add)
        );

        log.debug(
                "Order built successfully. Total amount={}",
                order.getTotalAmount()
        );

        return order;
    }

    /**
     * Generates an order number like:
     * ORD-20260708-AB12CD
     */
    private String generateOrderNumber() {

        return "ORD-"
                + java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE)
                + "-"
                + UUID.randomUUID()
                .toString()
                .substring(0, 6)
                .toUpperCase();
    }

}