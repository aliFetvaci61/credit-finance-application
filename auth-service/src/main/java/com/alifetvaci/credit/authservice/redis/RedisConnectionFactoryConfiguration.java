package com.alifetvaci.credit.authservice.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

@Configuration
@RequiredArgsConstructor
public class RedisConnectionFactoryConfiguration {

    private final RedisProperties redisProperties;

    private LettuceConnectionFactory getLettuceConnectionFactory() {
        final var port = redisProperties.getPort();
        final var host = redisProperties.getHost();
        final var password = redisProperties.getPassword();

        final var redisStandaloneConfiguration = new RedisStandaloneConfiguration();
        redisStandaloneConfiguration.setHostName(host);
        redisStandaloneConfiguration.setPort(port);
        redisStandaloneConfiguration.setPassword(password);
        return new LettuceConnectionFactory(redisStandaloneConfiguration,
                LettuceClientConfiguration.defaultConfiguration());
    }

    @Bean
    protected LettuceConnectionFactory redisConnectionFactory() {
        return getLettuceConnectionFactory();
    }


}