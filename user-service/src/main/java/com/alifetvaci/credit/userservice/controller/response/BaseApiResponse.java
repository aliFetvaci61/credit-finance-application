package com.alifetvaci.credit.userservice.controller.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BaseApiResponse<T> {
    private String message;
    private Boolean success=true;
    private T data;

    public BaseApiResponse(T data) {
        this.data = data;
    }

}