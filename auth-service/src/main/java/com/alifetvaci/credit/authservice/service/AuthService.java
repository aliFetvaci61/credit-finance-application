package com.alifetvaci.credit.authservice.service;

import com.alifetvaci.credit.authservice.controller.request.AuthRequest;
import com.alifetvaci.credit.authservice.controller.response.AuthResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    public AuthResponse createToken(AuthRequest request){

        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("firstname",request.getFirstName());
        extraClaims.put("lastname",request.getLastName());

        return AuthResponse.builder().token(jwtService.buildToken(extraClaims, request.getIdentificationNumber())).build();
    }
}
