package com.alifetvaci.creditservice.repository.model;

import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "INSTALLMENT")
@Entity
@ToString
public class Installment extends BaseEntity {

    @Column(name = "STATUS", nullable = false)
    private InstallmentStatus status;

    @Column(name = "AMOUNT", nullable = false)
    private BigDecimal amount;

    @Column(name = "DUE_DATE", updatable = false, nullable = false)
    private LocalDateTime dueDate;

    @ManyToOne
    @JoinColumn(name = "CREDIT_ID", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "FK_INSTALLMENT_CREDIT"))
    private Credit credit;

}
