package com.alifetvaci.credit.authservice.service;

import com.alifetvaci.credit.authservice.controller.request.AuthRequest;
import com.alifetvaci.credit.authservice.controller.response.AuthResponse;
import com.alifetvaci.credit.authservice.redis.model.Session;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtService jwtService;
    private final SessionService sessionService;

    public AuthResponse createToken(AuthRequest request) {
        // Generate a unique session ID
        String sessionId = UUID.randomUUID().toString();

        // Create a Session object with user information
        Session session = Session.builder()
                .identificationNumber(request.getIdentificationNumber())
                .firstname(request.getFirstName())
                .lastname(request.getLastName())
                .build();

        // Store the session in Redis with a 5-minute expiration
        sessionService.putValue(sessionId, TimeUnit.MINUTES, 5, session);

        // Build the JWT token with the session ID as the subject
        String token = jwtService.buildToken(sessionId);

        // Return the AuthResponse with the generated token
        return AuthResponse.builder()
                .token(token)
                .build();
    }
}
