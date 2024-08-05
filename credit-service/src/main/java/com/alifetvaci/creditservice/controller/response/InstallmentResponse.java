package com.alifetvaci.creditservice.controller.response;

import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class InstallmentResponse {

    private int id;

    private InstallmentStatus status;

    private BigDecimal amount;

    private LocalDateTime dueDate;

}
