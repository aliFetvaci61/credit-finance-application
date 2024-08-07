package com.alifetvaci.credit.userservice.api.exception;

public enum ErrorCode {
    USER_NOT_FOUND("User not found"),

    USER_ALREADY_EXISTS("User already exist"),
    AUTH_SERVICE_UNAVAILABLE("Auth service unavailable");


    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
