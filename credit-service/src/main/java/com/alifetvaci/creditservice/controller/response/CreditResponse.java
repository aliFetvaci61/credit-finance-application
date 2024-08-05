package com.alifetvaci.creditservice.controller.response;


import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreditResponse {

    private int id;

    private CreditStatus status;

    private BigDecimal amount;

    private List<InstallmentResponse> installments;

}
