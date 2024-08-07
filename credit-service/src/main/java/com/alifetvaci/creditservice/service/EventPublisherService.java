package com.alifetvaci.creditservice.service;

import com.alifetvaci.creditservice.repository.model.entity.Credit;
import com.alifetvaci.creditservice.repository.elastic.document.CreditDocument;
import com.alifetvaci.creditservice.repository.elastic.document.InstallmentDocument;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class EventPublisherService {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public EventPublisherService(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void publishCredit(CreditDocument event) {
        try {
            log.info("publishCredit -> publish is started, event: {}", event);
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send("credit", message);
            send.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("publishCredit -> publish is finished, event: {}", event);
                } else {
                    log.error("publishCredit -> publish is failed, event: {}", event);
                }
            });

        } catch (Exception e) {
            log.error("publishCredit -> publish is failed, event: {}", event, e);
        }
    }

    public void publishInstallment(InstallmentDocument event) {
        try {
            log.info("publishInstallment -> publish is started, event: {}", event);
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send("installment", message);
            send.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("publishInstallment -> publish is finished, event: {}", event);
                } else {
                    log.error("publishInstallment -> publish is failed, event: {}", event);
                }
            });

        } catch (Exception e) {
            log.error("publishInstallment -> publish is failed, event: {}", event, e);
        }
    }

    public void publishPaidCreditInstallment(Credit event) {
        try {
            log.info("PaidCreditInstallmentEventPublisher -> publish is started, event: {}", event);
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send("paid-credit-installment", message);
            send.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("PaidCreditInstallmentEventPublisher -> publish is finished, event: {}", event);
                } else {
                    log.error("PaidCreditInstallmentEventPublisher -> publish is failed, event: {}", event);
                }
            });

        } catch (Exception e) {
            log.error("PaidCreditInstallmentEventPublisher -> publish is failed, event: {}", event, e);
        }
    }

    public void publishOverdueCreditInstallment(CreditDocument event) {
        try {
            log.info("OverdueCreditInstallmentEventPublisher -> publish is started, event: {}", event);
            String message = objectMapper.writeValueAsString(event);
            CompletableFuture<SendResult<String, String>> send = kafkaTemplate.send("overdue-credit-installment", message);
            send.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("OverdueCreditInstallmentEventPublisher -> publish is finished, event: {}", event);
                } else {
                    log.error("OverdueCreditInstallmentEventPublisher -> publish is failed, event: {}", event);
                }
            });

        } catch (Exception e) {
            log.error("OverdueCreditInstallmentEventPublisher -> publish is failed, event: {}", event, e);
        }
    }
}
