package com.aayush.productservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    PRODUCT_NOT_FOUND(
            "PRD_001",
            "Product not found",
            HttpStatus.NOT_FOUND
    ),

    CATEGORY_NOT_FOUND(
            "CAT_001",
            "Category not found",
            HttpStatus.NOT_FOUND
    ),

    REVIEW_NOT_FOUND(
            "REV_001",
            "Review not found",
            HttpStatus.NOT_FOUND
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

    ErrorCode(String code, String message, HttpStatus status){
        this.code = code;
        this.message = message;
        this.status = status;
    }
}
