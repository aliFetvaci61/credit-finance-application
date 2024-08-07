package com.alifetvaci.creditservice.repository.model.entity;

import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;


@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "CREDIT")
@Entity
public class Credit extends BaseEntity{

    @Column(name = "STATUS", nullable = false)
    private CreditStatus status;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "IDENTIFICATION_NUMBER", nullable = false, updatable = false)
    private String identificationNumber;

}
