package com.alifetvaci.credit.userservice.delegate.auth.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthRequest {

    @NotBlank
    private String identificationNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;
}