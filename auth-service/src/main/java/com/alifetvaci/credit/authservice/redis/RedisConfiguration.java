package com.alifetvaci.credit.authservice.redis;

import com.alifetvaci.credit.authservice.redis.model.Session;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfiguration {

    private final LettuceConnectionFactory connectionFactory;

    public RedisConfiguration(LettuceConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;

    }

    @Bean
    public RedisTemplate<String, Session> sessionRedisTemplate() {
        final RedisTemplate<String, Session> redisTemplate = new RedisTemplate<>();
        var objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);

        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());

        redisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Session.class));
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(objectMapper, Session.class));

        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }
}