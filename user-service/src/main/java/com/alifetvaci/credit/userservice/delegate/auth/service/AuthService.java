package com.alifetvaci.credit.userservice.delegate.auth.service;

import com.alifetvaci.credit.userservice.api.exception.ErrorCode;
import com.alifetvaci.credit.userservice.api.exception.GenericException;
import com.alifetvaci.credit.userservice.controller.response.BaseApiResponse;
import com.alifetvaci.credit.userservice.delegate.auth.client.AuthClient;
import com.alifetvaci.credit.userservice.delegate.auth.request.AuthRequest;
import com.alifetvaci.credit.userservice.delegate.auth.response.AuthResponse;
import com.alifetvaci.credit.userservice.repository.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthClient authClient;

    public String createToken(User user){
        BaseApiResponse<AuthResponse> authResponse;
        try {
            authResponse = authClient.createToken(AuthRequest.builder().identificationNumber(user.getIdentificationNumber()).firstName(user.getFirstname()).lastName(user.getLastname()).build());
        }catch (Exception e){
            throw GenericException.builder()
                    .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                    .logMessage(this.getClass().getName() + ".createToken auth service unavailable {0} ", e.getMessage())
                    .message(ErrorCode.AUTH_SERVICE_UNAVAILABLE)
                    .build();
        }
        if (Boolean.TRUE.equals(authResponse.getSuccess()) && authResponse.getData() != null) {
            return authResponse.getData().getToken();
        }
        throw GenericException.builder()
                .httpStatus(HttpStatus.SERVICE_UNAVAILABLE)
                .logMessage(this.getClass().getName() + ".createToken auth service create token response not valid")
                .message(ErrorCode.AUTH_SERVICE_UNAVAILABLE)
                .build();

    }
}
