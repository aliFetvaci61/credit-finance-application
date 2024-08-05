package com.alifetvaci.credit.authservice.service;

import com.alifetvaci.credit.authservice.redis.model.Session;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class SessionService extends RedisService<Session> {

    public SessionService(RedisTemplate<String, Session> redisTemplate) {
        super(redisTemplate);
    }

}