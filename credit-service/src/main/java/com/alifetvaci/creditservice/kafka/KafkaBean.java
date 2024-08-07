package com.alifetvaci.creditservice.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
@Slf4j
public class KafkaBean {

    @Value(value = "${spring.kafka.consumer.bootstrap-servers}")
    private String bootstrapServers;


    @Bean
    public KafkaAdmin kafkaAdmin() {
        log.info("bootstrapServers: {}", bootstrapServers);
        Map<String, Object> map = new HashMap<>();
        map.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return new KafkaAdmin(map);
    }


    @Bean
    public NewTopic topic1() {
        return new NewTopic("credit", 1, (short) 1);
    }

    @Bean
    public NewTopic topic2() {
        return new NewTopic("installment", 1, (short) 1);
    }

    @Bean
    public NewTopic topic3() {
        return new NewTopic("overdue-credit-installment", 1, (short) 1);
    }

    @Bean
    public NewTopic topic4() {
        return new NewTopic("paid-credit-installment", 1, (short) 1);
    }



}