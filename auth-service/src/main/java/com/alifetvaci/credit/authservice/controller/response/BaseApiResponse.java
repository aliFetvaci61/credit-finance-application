package com.alifetvaci.credit.authservice.controller.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BaseApiResponse<T> {
    private String message;
    private boolean success=true;
    private T data;

    public BaseApiResponse(T data) {
        this.data = data;
    }

}