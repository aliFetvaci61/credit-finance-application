package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.api.exception.ErrorCode;
import com.alifetvaci.creditservice.api.exception.GenericException;
import com.alifetvaci.creditservice.controller.request.CreditCreateRequest;
import com.alifetvaci.creditservice.controller.response.CreditResponse;
import com.alifetvaci.creditservice.controller.response.InstallmentResponse;
import com.alifetvaci.creditservice.repository.CreditRepository;
import com.alifetvaci.creditservice.repository.ElasticCreditRepository;
import com.alifetvaci.creditservice.repository.ElasticInstallmentRepository;
import com.alifetvaci.creditservice.repository.InstallmentRepository;
import com.alifetvaci.creditservice.repository.model.Credit;
import com.alifetvaci.creditservice.repository.model.CreditDocument;
import com.alifetvaci.creditservice.repository.model.Installment;
import com.alifetvaci.creditservice.repository.model.InstallmentDocument;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;


@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final InstallmentRepository installmentRepository;
    private final EventPublisherService eventPublisherService;
    private final ElasticCreditRepository elasticCreditRepository;
    private final ElasticInstallmentRepository elasticInstallmentRepository;
    public void createCredit(CreditCreateRequest request, String identificationNumber) {
        Credit credit = creditRepository.save(Credit.builder().amount(request.getAmount()).status(CreditStatus.STARTED).identificationNumber(identificationNumber).build());
        BigDecimal installmentAmount = request.getAmount().divide(BigDecimal.valueOf(request.getInstallmentCount()));
        Calendar calendar = Calendar.getInstance();

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
            Installment installment = installmentRepository.save(Installment.builder()
                    .credit(credit)
                    .amount(installmentAmount)
                    .dueDate(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault()))
                    .status(InstallmentStatus.UNPAID)
                    .build());

            eventPublisherService.publishInstallment(getInstallmentDocumentFromInstallment(installment, credit));
            calendar.add(Calendar.DATE, minusDays);
        }
        eventPublisherService.publishCredit(getCreditDocumentFromCredit(credit));
    }


    public List<CreditResponse> getCredits(String identificationNumber) {
        List<CreditDocument> creditDocuments = elasticCreditRepository.findByIdentificationNumber(identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with this identification number {0}", identificationNumber)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

        return getCreditResponseList(creditDocuments);
    }

    public CreditResponse getCredit(String identificationNumber, int creditId) {
        CreditDocument creditDocument = elasticCreditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

        return getCreditResponseFromCreditDocument(creditDocument);
    }



    public CreditResponse getCreditInstallments(String identificationNumber, int creditId) {
        CreditDocument creditDocument = elasticCreditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

        List<InstallmentDocument> installmentDocuments = elasticInstallmentRepository.findByCreditId(creditDocument.getId()).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit installments not found with credit id {0}", creditDocument.getId())
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());

        return CreditResponse.builder().id(creditDocument.getId()).amount(creditDocument.getAmount()).status(creditDocument.getStatus()).installments(installmentDocuments.stream()
                    .map(installment -> InstallmentResponse.builder().id(installment.getId()).amount(installment.getAmount()).status(installment.getStatus()).dueDate(LocalDateTime.ofInstant(Instant.ofEpochMilli(installment.getDueDate()), TimeZone
                            .getDefault().toZoneId())).build())
                    .toList()).build();
    }

    @Transactional
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
        Installment savedInstallment = installmentRepository.save(installment);
        eventPublisherService.publishInstallment(getInstallmentDocumentFromInstallment(savedInstallment, credit));

        if (installments.stream().noneMatch(c -> c.getStatus() == InstallmentStatus.UNPAID)) {
            credit.setStatus(CreditStatus.FINISHED);
            creditRepository.save(credit);
            eventPublisherService.publishPaidCreditInstallment(credit);
        }
        eventPublisherService.publishCredit(getCreditDocumentFromCredit(credit));
    }


    private static CreditDocument getCreditDocumentFromCredit(Credit credit) {
        return CreditDocument.builder()
                .id(credit.getId())
                .amount(credit.getAmount())
                .status(credit.getStatus())
                .identificationNumber(credit.getIdentificationNumber())
                .deleted(credit.isDeleted())
                .build();
    }

    private static InstallmentDocument getInstallmentDocumentFromInstallment(Installment installment,Credit credit) {
        return InstallmentDocument.builder()
                .id(installment.getId())
                .amount(installment.getAmount())
                .status(installment.getStatus())
                .dueDate(installment.getDueDate().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli())
                .creditId(credit.getId())
                .deleted(installment.isDeleted())
                .build();
    }

    private static CreditResponse getCreditResponseFromCreditDocument(CreditDocument creditDocument) {
        return CreditResponse.builder()
                .id(creditDocument.getId())
                .amount(creditDocument.getAmount())
                .status(creditDocument.getStatus())
                .build();
    }

    private static List<CreditResponse> getCreditResponseList(List<CreditDocument> creditDocuments) {
        return creditDocuments.stream()
                .map(CreditService::getCreditResponseFromCreditDocument)
                .toList();
    }
}
