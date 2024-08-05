package com.alifetvaci.creditservice.api;


import com.alifetvaci.creditservice.controller.response.BaseApiResponse;

public class BaseController {

    protected <T> BaseApiResponse<T> success(T data) {
        var response = new BaseApiResponse<>(data);
        response.setSuccess(true);
        return response;
    }

}
