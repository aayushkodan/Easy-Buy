package com.aayush.easybuyinventoryservice.exception;

import lombok.Getter;

@Getter
public class EasybuyException extends RuntimeException {

    private final ErrorCode errorCode;

    public EasybuyException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public EasybuyException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

}
