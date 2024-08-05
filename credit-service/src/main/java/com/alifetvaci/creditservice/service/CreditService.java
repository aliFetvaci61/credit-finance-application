package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.api.exception.ErrorCode;
import com.alifetvaci.creditservice.api.exception.GenericException;
import com.alifetvaci.creditservice.controller.request.CreditCreateRequest;
import com.alifetvaci.creditservice.controller.response.CreditResponse;
import com.alifetvaci.creditservice.controller.response.InstallmentResponse;
import com.alifetvaci.creditservice.repository.CreditRepository;
import com.alifetvaci.creditservice.repository.InstallmentRepository;
import com.alifetvaci.creditservice.repository.model.Credit;
import com.alifetvaci.creditservice.repository.model.Installment;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import com.alifetvaci.creditservice.service.model.CreditInstallmentPublishEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditRepository creditRepository;
    private final InstallmentRepository installmentRepository;
    private final CreditInstallmentEventPublisher creditInstallmentEventPublisher;

    public void createCredit(CreditCreateRequest request, String identificationNumber){
        Credit credit = creditRepository.save(Credit.builder().amount(request.getAmount()).status(CreditStatus.STARTED).identificationNumber(identificationNumber).build());
        BigDecimal installmentAmount = request.getAmount().divide(BigDecimal.valueOf(request.getInstallmentCount()));
        Calendar calendar = Calendar.getInstance();
        List<Installment> installments =  new ArrayList<>();
        for (int i = 1; i <= request.getInstallmentCount(); i++){
            calendar.add(Calendar.MONTH, 1);
            int minusDays=0;
            if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY){
                calendar.add(Calendar.DATE, 2);
                minusDays=-2;
            }else if (calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY){
                calendar.add(Calendar.DATE, 1);
                minusDays=-1;
            }
            installments.add(installmentRepository.save(Installment.builder().credit(credit).amount(installmentAmount).dueDate(LocalDateTime.ofInstant(calendar.toInstant(), ZoneId.systemDefault())).status(InstallmentStatus.UNPAID).build()));
            calendar.add(Calendar.DATE,minusDays);
        }
        creditInstallmentEventPublisher.publish(CreditInstallmentPublishEvent.of(credit,installments));
    }

    public List<CreditResponse> getCredits(String identificationNumber){
        return creditRepository.findByIdentificationNumber(identificationNumber).stream()
                .map(credit -> CreditResponse.builder().id(credit.getId()).amount(credit.getAmount()).status(credit.getStatus()).build())
                .collect(Collectors.toList());
    }

    public CreditResponse getCredit(String identificationNumber, int creditId){
        Credit credit = creditRepository.findById(creditId).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());
        if(Objects.equals(credit.getIdentificationNumber(), identificationNumber)){
            return CreditResponse.builder().id(credit.getId()).amount(credit.getAmount()).status(credit.getStatus()).build();
        }
        throw GenericException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .logMessage(this.getClass().getName() + ".getCredit can not access to view this credit id {0}", creditId)
                .message(ErrorCode.FORBIDDEN_EXCEPTION)
                .build();

    }

    public CreditResponse getCreditInstallments(String identificationNumber, int creditId){
        Credit credit = creditRepository.findById(creditId).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());
        if(Objects.equals(credit.getIdentificationNumber(), identificationNumber)){
            return CreditResponse.builder().id(credit.getId()).amount(credit.getAmount()).status(credit.getStatus()).installments(installmentRepository.findByCreditAndStatus(credit, InstallmentStatus.UNPAID).stream()
                    .map(installment -> InstallmentResponse.builder().id(installment.getId()).amount(installment.getAmount()).status(installment.getStatus()).dueDate(installment.getDueDate()).build())
                    .collect(Collectors.toList())).build();
        }
        throw GenericException.builder()
                .httpStatus(HttpStatus.FORBIDDEN)
                .logMessage(this.getClass().getName() + ".getCredit can not access to view this credit id {0}", creditId)
                .message(ErrorCode.FORBIDDEN_EXCEPTION)
                .build();

    }

    public void payInstallment(String identificationNumber, int creditId, int installmentId){
        Credit credit = creditRepository.findById(creditId).orElseThrow(() -> GenericException.builder()
                .httpStatus(HttpStatus.NOT_FOUND)
                .logMessage(this.getClass().getName() + ".getCredit credit not found with credit id {0}", creditId)
                .message(ErrorCode.CREDIT_NOT_FOUND)
                .build());
        if(Objects.equals(credit.getIdentificationNumber(), identificationNumber)){
            Installment installment = installmentRepository.findById(installmentId).orElseThrow(() -> GenericException.builder()
                    .httpStatus(HttpStatus.NOT_FOUND)
                    .logMessage(this.getClass().getName() + ".getCredit installment not found with installment id {0}", installmentId)
                    .message(ErrorCode.INSTALLMENT_NOT_FOUND)
                    .build());
            installment.setStatus(InstallmentStatus.PAID);
            installmentRepository.save(installment);

            if(installmentRepository.findByCredit(credit).stream().noneMatch(c -> c.getStatus() == InstallmentStatus.UNPAID)){
                credit.setStatus(CreditStatus.FINISHED);
                creditRepository.save(credit);
            }
        }else{
            throw GenericException.builder()
                    .httpStatus(HttpStatus.FORBIDDEN)
                    .logMessage(this.getClass().getName() + ".getCredit can not access to view this credit id {0}", creditId)
                    .message(ErrorCode.FORBIDDEN_EXCEPTION)
                    .build();
        }


    }


}
