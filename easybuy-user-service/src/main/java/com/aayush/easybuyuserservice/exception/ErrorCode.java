package com.aayush.easybuyuserservice.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USR_001", "User not found", HttpStatus.NOT_FOUND),

    EMAIL_ALREADY_EXISTS("USR_002", "Email already exists", HttpStatus.CONFLICT),

    INVALID_CREDENTIALS("USR_003", "Invalid Credentials", HttpStatus.UNAUTHORIZED),

    INTERNAL_SERVER_ERROR("GEN_002", "Something went wrong", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus status;

    ErrorCode(String code, String message, HttpStatus status){
        this.code = code;
        this.message = message;
        this.status = status;
    }
}

