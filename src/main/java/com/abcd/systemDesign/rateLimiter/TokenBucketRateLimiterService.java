package com.abcd.systemDesign.rateLimiter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

@Service
public class TokenBucketRateLimiterService {

    private final StringRedisTemplate redisTemplate;
    private final long capacity;
    private final long refillTokens;
    private final long refillIntervalMillis;
    private final String keyPrefix = "token_bucket:";

    @Autowired
    public TokenBucketRateLimiterService(StringRedisTemplate redisTemplate, long capacity, long refillTokens, long refillIntervalMillis) {
        this.redisTemplate = redisTemplate;
        this.capacity = capacity;
        this.refillTokens = refillTokens;
        this.refillIntervalMillis = refillIntervalMillis;
    }

    public boolean tryConsume(String userId, long tokens) {
        String key = keyPrefix + userId;
        long now = Instant.now().toEpochMilli();

        // Initialize bucket if it doesn't exist
        redisTemplate.opsForValue().setIfAbsent(key + ":tokens", String.valueOf(capacity));
        redisTemplate.opsForValue().setIfAbsent(key + ":timestamp", String.valueOf(now));

        // Get current state
        long currentTokens = Long.parseLong(redisTemplate.opsForValue().get(key + ":tokens"));
        long lastRefillTime = Long.parseLong(redisTemplate.opsForValue().get(key + ":timestamp"));

        // Calculate tokens to refill
        long elapsedTime = now - lastRefillTime;
        long tokensToRefill = (elapsedTime / refillIntervalMillis) * refillTokens;
        long newTokens = Math.min(currentTokens + tokensToRefill, capacity);

        if (newTokens >= tokens) {
            // Consume tokens
            redisTemplate.opsForValue().set(key + ":tokens", String.valueOf(newTokens - tokens));
            redisTemplate.opsForValue().set(key + ":timestamp", String.valueOf(now));
            return true;
        } else {
            // Not enough tokens available
            redisTemplate.opsForValue().set(key + ":tokens", String.valueOf(newTokens));
            redisTemplate.opsForValue().set(key + ":timestamp", String.valueOf(now));
            return false;
        }
    }
}
