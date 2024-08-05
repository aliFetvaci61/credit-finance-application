package com.alifetvaci.creditservice.elastic;

import com.alifetvaci.creditservice.repository.ElasticCreditInstallmentRepository;
import com.alifetvaci.creditservice.service.model.CreditInstallment;
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
    private final ElasticCreditInstallmentRepository elasticCreditInstallmentRepository;


    @KafkaListener(topics = "credit-installment", groupId = "credit-service")
    @Retryable(value = RuntimeException.class, backoff = @Backoff(delay = 5000))
    public void updateIndex(String message) {
        try {
            log.info("ElasticCartConsumer -> consume is started, message: {}", message);
            CreditInstallment creditInstallmentPublishEvent = objectMapper.readValue(message, CreditInstallment.class);
            elasticCreditInstallmentRepository.save(creditInstallmentPublishEvent);
            log.info("ElasticCartConsumer -> consume is finished, message: {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
