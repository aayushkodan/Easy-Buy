package com.aayush.easybuycartorderservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    // =========================
    // CART ERRORS
    // =========================

    CART_NOT_FOUND(
            "CRT_001",
            "Cart not found",
            HttpStatus.NOT_FOUND
    ),

    CART_ALREADY_CHECKED_OUT(
            "CRT_002",
            "Cart has already been checked out",
            HttpStatus.BAD_REQUEST
    ),

    CART_ITEM_NOT_FOUND(
            "CRT_003",
            "Cart item not found",
            HttpStatus.NOT_FOUND
    ),

    CART_EMPTY(
            "CRT_004",
            "Cart is empty",
            HttpStatus.BAD_REQUEST
    ),

    PRODUCT_ALREADY_IN_CART(
            "CRT_005",
            "Product already exists in cart",
            HttpStatus.CONFLICT
    ),

    INVALID_CART_OPERATION(
            "CRT_006",
            "Invalid cart operation",
            HttpStatus.BAD_REQUEST
    ),

    // =========================
    // ORDER ERRORS
    // =========================

    ORDER_NOT_FOUND(
            "ORD_001",
            "Order not found",
            HttpStatus.NOT_FOUND
    ),

    ORDER_ALREADY_CANCELLED(
            "ORD_002",
            "Order is already cancelled",
            HttpStatus.BAD_REQUEST
    ),

    ORDER_CANNOT_BE_CANCELLED(
            "ORD_003",
            "Order cannot be cancelled in its current state",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_ORDER_STATUS(
            "ORD_004",
            "Invalid order status",
            HttpStatus.BAD_REQUEST
    ),

    ORDER_CREATION_FAILED(
            "ORD_005",
            "Failed to create order",
            HttpStatus.BAD_REQUEST
    ),

    // =========================
    // PRODUCT ERRORS
    // =========================

    PRODUCT_NOT_FOUND(
            "PRD_001",
            "Product not found",
            HttpStatus.NOT_FOUND
    ),

    PRODUCT_INACTIVE(
            "PRD_002",
            "Product is inactive",
            HttpStatus.BAD_REQUEST
    ),

    // =========================
    // INVENTORY ERRORS
    // =========================

    INVENTORY_NOT_FOUND(
            "INV_001",
            "Inventory not found",
            HttpStatus.NOT_FOUND
    ),

    INSUFFICIENT_STOCK(
            "INV_002",
            "Insufficient stock available",
            HttpStatus.BAD_REQUEST
    ),

    STOCK_RESERVATION_FAILED(
            "INV_003",
            "Failed to reserve stock",
            HttpStatus.BAD_REQUEST
    ),

    STOCK_RELEASE_FAILED(
            "INV_004",
            "Failed to release stock",
            HttpStatus.BAD_REQUEST
    ),

    // =========================
    // USER ERRORS
    // =========================

    USER_NOT_FOUND(
            "USR_001",
            "User not found",
            HttpStatus.NOT_FOUND
    ),

    // =========================
    // PAYMENT ERRORS
    // =========================

    PAYMENT_FAILED(
            "PAY_001",
            "Payment failed",
            HttpStatus.BAD_REQUEST
    ),

    PAYMENT_PENDING(
            "PAY_002",
            "Payment is pending",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_PAYMENT_METHOD(
            "PAY_003",
            "Invalid payment method",
            HttpStatus.BAD_REQUEST
    ),

    // =========================
    // GENERIC ERRORS
    // =========================

    INVALID_REQUEST(
            "GEN_001",
            "Invalid request",
            HttpStatus.BAD_REQUEST
    ),

    INTERNAL_SERVER_ERROR(
            "GEN_002",
            "Something went wrong",
            HttpStatus.INTERNAL_SERVER_ERROR
    );

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status) {
        this.code = code;
        this.message = message;
        this.status = status;
    }
}