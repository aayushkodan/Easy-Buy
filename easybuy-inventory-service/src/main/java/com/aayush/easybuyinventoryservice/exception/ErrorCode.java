package com.aayush.easybuyinventoryservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    INVENTORY_NOT_FOUND(
            "INV_001",
            "Inventory not found",
            HttpStatus.NOT_FOUND
    ),

    INVENTORY_ALREADY_EXISTS(
            "INV_002",
            "Inventory already exists for this product",
            HttpStatus.CONFLICT
    ),

    PRODUCT_NOT_FOUND(
            "INV_003",
            "Product not found",
            HttpStatus.NOT_FOUND
    ),

    SKU_ALREADY_EXISTS(
            "INV_004",
            "SKU already exists",
            HttpStatus.CONFLICT
    ),

    INSUFFICIENT_STOCK(
            "INV_005",
            "Insufficient stock available",
            HttpStatus.BAD_REQUEST
    ),

    INVALID_STOCK_OPERATION(
            "INV_006",
            "Invalid stock operation",
            HttpStatus.BAD_REQUEST
    ),

    STOCK_RESERVATION_FAILED(
            "INV_007",
            "Unable to reserve stock",
            HttpStatus.BAD_REQUEST
    ),

    STOCK_RELEASE_FAILED(
            "INV_008",
            "Unable to release reserved stock",
            HttpStatus.BAD_REQUEST
    ),

    LOW_STOCK(
            "INV_009",
            "Product stock is below reorder level",
            HttpStatus.BAD_REQUEST
    ),

    INVENTORY_INACTIVE(
            "INV_010",
            "Inventory item is inactive",
            HttpStatus.BAD_REQUEST
    ),

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
