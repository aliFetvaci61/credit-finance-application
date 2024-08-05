package com.alifetvaci.creditservice.service.model;

import com.alifetvaci.creditservice.controller.response.InstallmentResponse;
import com.alifetvaci.creditservice.repository.model.Credit;
import com.alifetvaci.creditservice.repository.model.Installment;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
public class CreditInstallmentPublishEvent {

    private int id;

    private CreditStatus status;

    private BigDecimal amount;

    private List<Installment> installments;

    public CreditInstallmentPublishEvent(Credit credit, List<Installment> installments) {
        this.id = credit.getId();
        this.status = credit.getStatus();
        this.amount = credit.getAmount();
        this.installments = installments;
    }

    public static CreditInstallmentPublishEvent of(Credit credit, List<Installment> installments) {
        return new CreditInstallmentPublishEvent(credit,installments);
    }

}
