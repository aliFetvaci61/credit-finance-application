package com.alifetvaci.credit.userservice.controller.request;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class LoginRequest {

    @NotBlank
    //@ValidIdentificationNumber
    private String identificationNumber;

    @NotBlank
    private String password;

}
