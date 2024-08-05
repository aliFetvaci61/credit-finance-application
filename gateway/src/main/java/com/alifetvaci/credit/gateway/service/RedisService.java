package com.alifetvaci.credit.gateway.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.concurrent.TimeUnit;

public class RedisService <T> {

    private final RedisTemplate<String, T> redisTemplate;
    private final ValueOperations<String, T> valueOperations;

    public RedisService(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.valueOperations = redisTemplate.opsForValue();
    }

    public T getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    public void putValue(String key, TimeUnit expirationTimeUnit, int expirationTime, T value) {
        valueOperations.set(key, value, expirationTime, expirationTimeUnit);
    }

    public void deleteValue(String key) {
        valueOperations.getOperations().delete(key);
    }

}