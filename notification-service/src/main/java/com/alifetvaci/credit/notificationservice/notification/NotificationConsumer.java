package com.alifetvaci.credit.notificationservice.notification;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class NotificationConsumer {

    @KafkaListener(topics = "paid-credit-installment", groupId = "notification-service")
    @Retryable(value = RuntimeException.class, backoff = @Backoff(delay = 5000))
    public void consumePaidCreditInstallment(String message) {
        try {
            log.info("Paid credit installment {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @KafkaListener(topics = "overdue-credit-installment", groupId = "notification-service")
    @Retryable(value = RuntimeException.class, backoff = @Backoff(delay = 5000))
    public void consumeOverdueCreditInstallment(String message) {
        try {
            log.info("Overdue credit installment {}", message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
