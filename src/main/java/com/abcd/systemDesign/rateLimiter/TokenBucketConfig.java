package com.abcd.systemDesign.rateLimiter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

@Configuration
public class TokenBucketConfig {

    @Bean
    public TokenBucketRateLimiterService tokenBucketRateLimiterService(StringRedisTemplate redisTemplate) {
        long capacity = 100; // Example capacity
        long refillTokens = 10; // Example refill tokens
        long refillIntervalMillis = 100000; // Example refill interval in milliseconds
        return new TokenBucketRateLimiterService(redisTemplate, capacity, refillTokens, refillIntervalMillis);
    }
}
