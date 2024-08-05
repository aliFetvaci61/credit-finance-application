package com.alifetvaci.credit.userservice.api;


import com.alifetvaci.credit.userservice.controller.response.BaseApiResponse;

public class BaseController {

    protected <T> BaseApiResponse<T> success(T data) {
        var response = new BaseApiResponse<>(data);
        response.setSuccess(true);
        return response;
    }

}
