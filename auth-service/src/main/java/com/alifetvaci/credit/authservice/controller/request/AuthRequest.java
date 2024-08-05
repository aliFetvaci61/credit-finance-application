package com.alifetvaci.credit.authservice.controller.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;


@Getter
public class AuthRequest {

    @NotBlank
    private String identificationNumber;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

}
