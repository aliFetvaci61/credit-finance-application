package com.alifetvaci.creditservice.service.model;

import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


import java.math.BigDecimal;


@Getter
@Setter
@Builder
public class InstallmentPublishEvent {

    private int id;

    private InstallmentStatus status;

    private BigDecimal amount;

    private long dueDate;

    private int creditId;
}
