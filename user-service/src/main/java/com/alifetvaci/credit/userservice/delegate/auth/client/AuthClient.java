package com.alifetvaci.credit.userservice.delegate.auth.client;

import com.alifetvaci.credit.userservice.controller.response.BaseApiResponse;
import com.alifetvaci.credit.userservice.delegate.auth.request.AuthRequest;
import com.alifetvaci.credit.userservice.delegate.auth.response.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "auth-service-client", url = "${service.auth.url}", path = "/auth-service")
public interface AuthClient {

    @PostMapping("/api/v1/token")
    BaseApiResponse<AuthResponse> createToken(@RequestBody AuthRequest request);

}
