package com.alifetvaci.creditservice.scheduler;

import com.alifetvaci.creditservice.repository.ElasticCreditRepository;
import com.alifetvaci.creditservice.repository.ElasticInstallmentRepository;
import com.alifetvaci.creditservice.repository.model.CreditDocument;
import com.alifetvaci.creditservice.repository.model.InstallmentDocument;
import com.alifetvaci.creditservice.service.EventPublisherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

@RequiredArgsConstructor
@Slf4j
@Service
public class OverdueCreditInstallmentScheduler {

    private final ElasticInstallmentRepository elasticInstallmentRepository;

    private final ElasticCreditRepository  elasticCreditRepository;

    private final EventPublisherService eventPublisherService;


    // at 12:00 AM every day
    @Scheduled(cron="0 0 0 * * ?")
    public void scheduleOverdueCreditInstallment() {
        List<InstallmentDocument> installmentDocuments = elasticInstallmentRepository.findInstallmentDocumentByDueDateBefore(System.currentTimeMillis()).orElse(Collections.emptyList());
        installmentDocuments.forEach(installmentDocument -> {
            CreditDocument creditDocument = elasticCreditRepository.findById(installmentDocument.getCreditId()).orElse(null);
            if (Objects.nonNull(creditDocument)) {
                eventPublisherService.publishOverdueCreditInstallment(creditDocument);
            }
        });
    }
}
