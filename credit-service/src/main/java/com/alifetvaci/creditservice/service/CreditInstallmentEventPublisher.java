package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.service.model.CreditInstallment;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class CreditInstallmentEventPublisher {
    @Value(value = "${kafka.topic:credit-installment}")
    private String topic;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public CreditInstallmentEventPublisher(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(CreditInstallment event) {
        try {
            log.info("CreditInstallmentEventPublisher -> publish is started, event: {}", event);
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send(topic, message);
            send.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("CreditInstallmentEventPublisher -> publish is finished, event: {}", event);
                } else {
                    log.error("CreditInstallmentEventPublisher -> publish is failed, event: {}", event);
                }
            });

        } catch (Exception e) {
            log.error("CreditInstallmentEventPublisher -> publish is failed, event: {}", event, e);
        }
    }
}
