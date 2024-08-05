package com.alifetvaci.credit.authservice.service;

import com.alifetvaci.credit.authservice.controller.request.AuthRequest;
import com.alifetvaci.credit.authservice.controller.response.AuthResponse;
import com.alifetvaci.credit.authservice.redis.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;

    private  final SessionService sessionService;

    public AuthResponse createToken(AuthRequest request){

        Map<String, Object> extraClaims = new HashMap<>();
        UUID uuid = UUID.randomUUID();
        sessionService.putValue(uuid.toString(), TimeUnit.MINUTES, 5, Session.builder().identificationNumber(request.getIdentificationNumber()).firstname(request.getFirstName()).lastname(request.getLastName()).build());
        return AuthResponse.builder().token(jwtService.buildToken(extraClaims, uuid.toString())).build();
    }
}
