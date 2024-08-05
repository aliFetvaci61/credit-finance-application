package com.alifetvaci.credit.authservice.api;

import com.alifetvaci.credit.authservice.controller.response.BaseApiResponse;

public class BaseController {

    protected <T> BaseApiResponse<T> success(T data) {
        var response = new BaseApiResponse<>(data);
        response.setSuccess(true);
        return response;
    }

}