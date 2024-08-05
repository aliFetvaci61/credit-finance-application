package com.alifetvaci.creditservice.service.model;

import com.alifetvaci.creditservice.repository.model.Credit;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@ToString
@Document(indexName = "credit-installment")
@AllArgsConstructor
@NoArgsConstructor
public class CreditInstallment {

    @Id
    private int id;

    private CreditStatus status;

    private BigDecimal amount;

    private String identificationNumber;

    private List<InstallmentPublishEvent> installments;

    public CreditInstallment(Credit credit, List<InstallmentPublishEvent> installments) {
        this.id = credit.getId();
        this.status = credit.getStatus();
        this.amount = credit.getAmount();
        this.identificationNumber = credit.getIdentificationNumber();
        this.installments = installments;
    }

    public static CreditInstallment of(Credit credit, List<InstallmentPublishEvent> installments) {
        return new CreditInstallment(credit,installments);
    }

}
