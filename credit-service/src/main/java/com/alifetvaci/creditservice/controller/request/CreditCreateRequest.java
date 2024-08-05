package com.alifetvaci.creditservice.controller.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class CreditCreateRequest {

    @Min(1)
    private BigDecimal amount;

    @Min(1)
    private int installmentCount;

}
