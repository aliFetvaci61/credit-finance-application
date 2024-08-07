package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.api.exception.ErrorCode;
import com.alifetvaci.creditservice.api.exception.GenericException;
import com.alifetvaci.creditservice.controller.request.CreditCreateRequest;
import com.alifetvaci.creditservice.controller.response.CreditResponse;
import com.alifetvaci.creditservice.repository.CreditRepository;
import com.alifetvaci.creditservice.repository.InstallmentRepository;
import com.alifetvaci.creditservice.repository.elastic.ElasticCreditRepository;
import com.alifetvaci.creditservice.repository.elastic.ElasticInstallmentRepository;
import com.alifetvaci.creditservice.repository.model.entity.Credit;
import com.alifetvaci.creditservice.repository.elastic.document.CreditDocument;
import com.alifetvaci.creditservice.repository.model.entity.Installment;
import com.alifetvaci.creditservice.repository.elastic.document.InstallmentDocument;
import com.alifetvaci.creditservice.repository.model.enums.CreditStatus;
import com.alifetvaci.creditservice.repository.model.enums.InstallmentStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CreditServiceTest {

    @Mock
    private CreditRepository creditRepository;

    @Mock
    private InstallmentRepository installmentRepository;

    @Mock
    private EventPublisherService eventPublisherService;

    @Mock
    private ElasticCreditRepository elasticCreditRepository;

    @Mock
    private ElasticInstallmentRepository elasticInstallmentRepository;

    @InjectMocks
    private CreditService creditService;

    @Test
    void createCredit() {
        // Arrange
        CreditCreateRequest request = new CreditCreateRequest(BigDecimal.valueOf(1000), 5);
        String identificationNumber = "1234567890";
        Credit credit = Credit.builder()
                .id(1)
                .amount(request.getAmount())
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        when(creditRepository.save(any(Credit.class))).thenReturn(credit);

        // Act
        creditService.createCredit(request, identificationNumber);

        // Assert
        verify(creditRepository, times(1)).save(any(Credit.class));
        verify(eventPublisherService, times(1)).publishCredit(any(CreditDocument.class));
    }

    @Test
    void getCredits() {
        // Arrange
        String identificationNumber = "1234567890";
        CreditDocument creditDocument = CreditDocument.builder()
                .id(1)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        List<CreditDocument> creditDocuments = new ArrayList<>();
        creditDocuments.add(creditDocument);
        when(elasticCreditRepository.findByIdentificationNumber(identificationNumber)).thenReturn(Optional.of(creditDocuments));

        // Act
        List<CreditResponse> creditResponses = creditService.getCredits(identificationNumber);

        // Assert
        assertEquals(1, creditResponses.size());
        assertEquals(creditDocument.getId(), creditResponses.get(0).getId());
        assertEquals(creditDocument.getAmount(), creditResponses.get(0).getAmount());
        assertEquals(creditDocument.getStatus(), creditResponses.get(0).getStatus());
    }

    @Test
    void getCredit() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        CreditDocument creditDocument = CreditDocument.builder()
                .id(creditId)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        when(elasticCreditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.of(creditDocument));

        // Act
        CreditResponse creditResponse = creditService.getCredit(identificationNumber, creditId);

        // Assert
        assertEquals(creditDocument.getId(), creditResponse.getId());
        assertEquals(creditDocument.getAmount(), creditResponse.getAmount());
        assertEquals(creditDocument.getStatus(), creditResponse.getStatus());
    }

    @Test
    void getCreditInstallments() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        CreditDocument creditDocument = CreditDocument.builder()
                .id(creditId)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        InstallmentDocument installmentDocument = InstallmentDocument.builder()
                .id(1)
                .amount(BigDecimal.valueOf(200))
                .status(InstallmentStatus.UNPAID)
                .dueDate(Instant.now().toEpochMilli())
                .creditId(creditId)
                .build();
        List<InstallmentDocument> installmentDocuments = new ArrayList<>();
        installmentDocuments.add(installmentDocument);
        when(elasticCreditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.of(creditDocument));
        when(elasticInstallmentRepository.findByCreditId(creditId)).thenReturn(Optional.of(installmentDocuments));

        // Act
        CreditResponse creditResponse = creditService.getCreditInstallments(identificationNumber, creditId);

        // Assert
        assertEquals(creditDocument.getId(), creditResponse.getId());
        assertEquals(creditDocument.getAmount(), creditResponse.getAmount());
        assertEquals(creditDocument.getStatus(), creditResponse.getStatus());
        assertEquals(1, creditResponse.getInstallments().size());
        assertEquals(installmentDocument.getId(), creditResponse.getInstallments().get(0).getId());
        assertEquals(installmentDocument.getAmount(), creditResponse.getInstallments().get(0).getAmount());
        assertEquals(installmentDocument.getStatus(), creditResponse.getInstallments().get(0).getStatus());
        assertEquals(LocalDateTime.ofInstant(Instant.ofEpochMilli(installmentDocument.getDueDate()), TimeZone.getDefault().toZoneId()), creditResponse.getInstallments().get(0).getDueDate());
    }

    @Test
    void payInstallment_success() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        int installmentId = 1;
        Credit credit = Credit.builder()
                .id(creditId)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        Installment installment = Installment.builder()
                .id(installmentId)
                .amount(BigDecimal.valueOf(200))
                .status(InstallmentStatus.UNPAID)
                .dueDate(LocalDateTime.now())
                .credit(credit)
                .build();
        when(creditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.of(credit));
        when(installmentRepository.findByCredit(credit)).thenReturn(List.of(installment));

        // Act
        creditService.payInstallment(identificationNumber, creditId, installmentId);

        // Assert
        verify(installmentRepository, times(1)).save(any(Installment.class));
        verify(eventPublisherService, times(1)).publishInstallment(any(InstallmentDocument.class));
        verify(eventPublisherService, times(1)).publishCredit(any(CreditDocument.class));
    }

    @Test
    void payInstallment_creditNotFound() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        int installmentId = 1;
        when(creditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(GenericException.class, () -> creditService.payInstallment(identificationNumber, creditId, installmentId));
        verify(installmentRepository, never()).save(any(Installment.class));
        verify(eventPublisherService, never()).publishInstallment(any(InstallmentDocument.class));
        verify(eventPublisherService, never()).publishCredit(any(CreditDocument.class));
    }

    @Test
    void payInstallment_installmentNotFound() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        int installmentId = 1;
        Credit credit = Credit.builder()
                .id(creditId)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        when(creditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.of(credit));
        when(installmentRepository.findByCredit(credit)).thenReturn(List.of());

        // Act & Assert
        assertThrows(GenericException.class, () -> creditService.payInstallment(identificationNumber, creditId, installmentId));
        verify(installmentRepository, never()).save(any(Installment.class));
        verify(eventPublisherService, never()).publishInstallment(any(InstallmentDocument.class));
        verify(eventPublisherService, never()).publishCredit(any(CreditDocument.class));
    }

    @Test
    void payInstallment_alreadyPaid() {
        // Arrange
        String identificationNumber = "1234567890";
        int creditId = 1;
        int installmentId = 1;
        Credit credit = Credit.builder()
                .id(creditId)
                .amount(BigDecimal.valueOf(1000))
                .status(CreditStatus.STARTED)
                .identificationNumber(identificationNumber)
                .build();
        Installment installment = Installment.builder()
                .id(installmentId)
                .amount(BigDecimal.valueOf(200))
                .status(InstallmentStatus.PAID)
                .dueDate(LocalDateTime.now())
                .credit(credit)
                .build();
        when(creditRepository.findByIdAndIdentificationNumber(creditId, identificationNumber)).thenReturn(Optional.of(credit));
        when(installmentRepository.findByCredit(credit)).thenReturn(List.of(installment));

        // Act & Assert
        assertThrows(GenericException.class, () -> creditService.payInstallment(identificationNumber, creditId, installmentId));
        verify(installmentRepository, never()).save(any(Installment.class));
        verify(eventPublisherService, never()).publishInstallment(any(InstallmentDocument.class));
        verify(eventPublisherService, never()).publishCredit(any(CreditDocument.class));
    }

    @Test
    void createCreditNotFoundException() {
        // Act
        GenericException exception = creditService.createCreditNotFoundException(1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorCode.CREDIT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    void createInstallmentNotFoundException() {
        // Act
        GenericException exception = creditService.createInstallmentNotFoundException(1);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(ErrorCode.INSTALLMENT_NOT_FOUND, exception.getErrorCode());
    }
}