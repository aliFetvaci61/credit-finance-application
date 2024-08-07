package com.alifetvaci.creditservice.controller.request;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class CreditCreateRequest {

    @Min(1)
    private BigDecimal amount;

    @Min(1)
    private int installmentCount;

}
