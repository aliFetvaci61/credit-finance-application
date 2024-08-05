package com.alifetvaci.credit.userservice.controller;


import com.alifetvaci.credit.userservice.api.BaseController;
import com.alifetvaci.credit.userservice.controller.request.LoginRequest;
import com.alifetvaci.credit.userservice.controller.request.RegisterRequest;
import com.alifetvaci.credit.userservice.controller.response.BaseApiResponse;
import com.alifetvaci.credit.userservice.controller.response.LoginResponse;
import com.alifetvaci.credit.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController extends BaseController {

   private final UserService service;

    @PostMapping("api/v1/register")
    public BaseApiResponse<Void> registerUser(@RequestBody  @Valid RegisterRequest request) {
        service.register(request);
        return success(null);
    }

    @PostMapping("api/v1/login")
    public BaseApiResponse<LoginResponse> loginUser(@RequestBody  @Valid LoginRequest request) {
        return success(service.login(request));
    }

}
