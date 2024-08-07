package com.alifetvaci.creditservice.elastic;

import com.alifetvaci.creditservice.repository.elastic.ElasticCreditRepository;
import com.alifetvaci.creditservice.repository.elastic.ElasticInstallmentRepository;
import com.alifetvaci.creditservice.repository.elastic.document.CreditDocument;
import com.alifetvaci.creditservice.repository.elastic.document.InstallmentDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class ElasticCreditInstallmentSyncConsumer {

    private final ObjectMapper objectMapper;
    private final ElasticCreditRepository elasticCreditRepository;
    private final ElasticInstallmentRepository elasticInstallmentRepository;


    @KafkaListener(topics = "credit", groupId = "credit-service")
    @Retryable(value = RuntimeException.class, backoff = @Backoff(delay = 5000))
    public void updateCreditIndex(String message) {
        try {
            log.info("ElasticCreditConsumer -> consume is started, message: {}", message);
            CreditDocument creditDocument = objectMapper.readValue(message, CreditDocument.class);
            elasticCreditRepository.save(creditDocument);
            log.info("ElasticCreditConsumer -> consume is finished, message: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "installment", groupId = "credit-service")
    @Retryable(value = RuntimeException.class, backoff = @Backoff(delay = 5000))
    public void updateInstallmentIndex(String message) {
        try {
            log.info("ElasticInstallmentConsumer -> consume is started, message: {}", message);
            InstallmentDocument installmentDocument = objectMapper.readValue(message, InstallmentDocument.class);
            elasticInstallmentRepository.save(installmentDocument);
            log.info("ElasticInstallmentConsumer -> consume is finished, message: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
