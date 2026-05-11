package com.aayush.productservice.exception;

import lombok.Getter;

@Getter
public class EasyBuyException extends RuntimeException {

    private final ErrorCode errorCode;

    public EasyBuyException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public EasyBuyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
