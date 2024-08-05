package com.alifetvaci.creditservice.api.exception;

public enum ErrorCode {
    CREDIT_NOT_FOUND("Credit not found"),

    INSTALLMENT_NOT_FOUND("installment not found"),

    FORBIDDEN_EXCEPTION("Can not access to view this credit");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
