package com.alifetvaci.credit.authservice.controller;



import com.alifetvaci.credit.authservice.api.BaseController;
import com.alifetvaci.credit.authservice.controller.request.AuthRequest;
import com.alifetvaci.credit.authservice.controller.response.AuthResponse;
import com.alifetvaci.credit.authservice.controller.response.BaseApiResponse;
import com.alifetvaci.credit.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController extends BaseController {

    private final AuthService service;

    @PostMapping("api/v1/token")
    public BaseApiResponse<AuthResponse> createToken(@RequestBody @Valid AuthRequest request) {
        return success(service.createToken(request));
    }

}
