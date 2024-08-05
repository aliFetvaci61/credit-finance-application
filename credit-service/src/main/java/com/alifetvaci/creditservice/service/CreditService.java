package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.api.exception.ErrorCode;
import com.alifetvaci.creditservice.api.exception.GenericException;
import com.alifetvaci.creditservice.controller.request.CreditCreateRequest;
import com.alifetvaci.creditservice.controller.response.CreditResponse;
import com.alifetvaci.creditservice.controller.response.InstallmentResponse;
import com.alifetvaci.creditservice.repository.CreditRepository;
import com.alifetvaci.creditservice.repository.ElasticCreditInstallmentRepository;
import com.alifetvaci.creditservice.repository.InstallmentRepository;
import com.alifetvaci.creditservice.repository.model.Credit;
import com.alifetvaci.creditservice.repository.model.Installment;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import com.alifetvaci.creditservice.service.model.CreditInstallment;
import com.alifetvaci.creditservice.service.model.InstallmentPublishEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final InstallmentRepository installmentRepository;
    private final CreditInstallmentEventPublisher creditInstallmentEventPublisher;
    private final ElasticCreditInstallmentRepository elasticCreditInstallmentRepository;

    public void createCredit(CreditCreateRequest request, String identificationNumber) {
        Credit credit = creditRepository.save(Credit.builder().amount(request.getAmount()).status(CreditStatus.STARTED).identificationNumber(identificationNumber).build());
        BigDecimal installmentAmount = request.getAmount().divide(BigDecimal.valueOf(request.getInstallmentCount()));
        Calendar calendar = Calendar.getInstance();
        List<InstallmentPublishEvent> installments = new ArrayList<>();
        for (int i = 1; i <= request.getInstallmentCount(); i++) {
            calendar.add(Calendar.DATE, 30);
            int minusDays = 0;
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
                calendar.add(Calendar.DATE, 2);
                minusDays = -2;
            } else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                calendar.add(Calendar.DATE, 1);
                minusDays = -1;
            }
            Installment installment = installmentRepository.save(Installment.builder().credit(credit).amount(installmentAmount).dueDate(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())).status(InstallmentStatus.UNPAID).build());
            installments.add(InstallmentPublishEvent.builder().id(installment.getId()).status(installment.getStatus()).creditId(credit.getId()).amount(installment.getAmount()).dueDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build());
            calendar.add(Calendar.DATE, minusDays);
        }
        creditInstallmentEventPublisher.publish(CreditInstallment.of(credit, installments));
    }

    public List<CreditResponse> getCredits(String identificationNumber) {
        List<CreditInstallment> creditInstallments = elasticCreditInstallmentRepository.findByIdentificationNumber(identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with this identification number {0}", identificationNumber)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

        return creditInstallments.stream()
                .map(credit -> CreditResponse.builder().id(credit.getId()).amount(credit.getAmount()).status(credit.getStatus()).build())
                .toList();
    }

    public CreditResponse getCredit(String identificationNumber, int creditId) {
        CreditInstallment creditInstallment = elasticCreditInstallmentRepository.findByIdAndIdentificationNumber(creditId, identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

            return CreditResponse.builder().id(creditInstallment.getId()).amount(creditInstallment.getAmount()).status(creditInstallment.getStatus()).build();
    }

    public CreditResponse getCreditInstallments(String identificationNumber, int creditId) {
        CreditInstallment creditInstallment = elasticCreditInstallmentRepository.findByIdAndIdentificationNumber(creditId,identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());
            return CreditResponse.builder().id(creditInstallment.getId()).amount(creditInstallment.getAmount()).status(creditInstallment.getStatus()).installments(creditInstallment.getInstallments().stream()
                    .map(installment -> InstallmentResponse.builder().id(installment.getId()).amount(installment.getAmount()).status(installment.getStatus()).dueDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(installment.getDueDate()), TimeZone
                            .getDefault().toZoneId())).build())
                    .toList()).build();
    }

    public void payInstallment(String identificationNumber, int creditId, int installmentId) {
        Credit credit = creditRepository.findByIdAndIdentificationNumber(creditId,identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());
        List<Installment> installments = installmentRepository.findByCredit(credit);
        Installment installment = installments.stream().filter(c -> c.getStatus() == InstallmentStatus.UNPAID).toList().stream().filter(c -> c.getId() == installmentId).findFirst().orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit unpaid installment not found with installment id {0}", installmentId)
                .message(ErrorCode.INSTALLMENT_NOT_FOUND)
                .build());
        installment.setStatus(InstallmentStatus.PAID);
        installmentRepository.save(installment);
        if (installments.stream().noneMatch(c -> c.getStatus() == InstallmentStatus.UNPAID)) {
            credit.setStatus(CreditStatus.FINISHED);
            creditRepository.save(credit);
            //TODO Notification send to kafka for credit is finished
        }
        List<InstallmentPublishEvent> installmentPublishEvents = installments.stream().map(i -> InstallmentPublishEvent.builder().id(i.getId()).amount(i.getAmount()).status(i.getStatus()).creditId(creditId).dueDate(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()).build()).toList();
        creditInstallmentEventPublisher.publish(CreditInstallment.of(credit, installmentPublishEvents));
    }
}
