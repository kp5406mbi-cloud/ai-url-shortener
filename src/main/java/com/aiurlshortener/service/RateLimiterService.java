package com.aiurlshortener.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RateLimiterService {

    private static final Logger logger =
            LoggerFactory.getLogger(RateLimiterService.class);

    /**
     * Redis key format:
     * rate_limit:ip:127.0.0.1
     */
    private static final String KEY_PREFIX = "rate_limit:ip:";

    private final StringRedisTemplate redisTemplate;

    @Value("${rate.limit.maxRequests}")
    private int maxRequests;

    @Value("${rate.limit.windowSeconds}")
    private int windowSeconds;

    public RateLimiterService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Returns true if request is allowed.
     * Returns false if request exceeds rate limit.
     */
    public boolean allowRequest(String ipAddress) {

        String key = KEY_PREFIX + ipAddress;

        Long count = redisTemplate.opsForValue().increment(key);

        if (count == null) {
            logger.error("Redis returned null while incrementing key {}", key);
            return false;
        }

        /*
         * First request in the current time window.
         * Set expiration only once.
         */
        if (count == 1) {
            redisTemplate.expire(key, windowSeconds, TimeUnit.SECONDS);
        }

        boolean allowed = count <= maxRequests;

        if (!allowed) {
            logger.warn("Rate limit exceeded for IP: {}", ipAddress);
        }

        return allowed;
    }

    /**
     * Returns remaining requests available in the current window.
     */
    public long getRemainingRequests(String ipAddress) {

        String key = KEY_PREFIX + ipAddress;

        String value = redisTemplate.opsForValue().get(key);

        long currentRequests = 0;

        if (value != null) {
            currentRequests = Long.parseLong(value);
        }

        return Math.max(0, maxRequests - currentRequests);
    }

    /**
     * Returns remaining time (in seconds)
     * before the rate limit resets.
     */
    public long getRetryAfter(String ipAddress) {

        String key = KEY_PREFIX + ipAddress;

        Long ttl = redisTemplate.getExpire(key);

        if (ttl == null || ttl < 0) {
            return 0;
        }

        return ttl;
    }

    /**
     * Returns current request count.
     */
    public long getCurrentRequestCount(String ipAddress) {

        String key = KEY_PREFIX + ipAddress;

        String value = redisTemplate.opsForValue().get(key);

        if (value == null) {
            return 0;
        }

        return Long.parseLong(value);
    }

    /**
     * Clears the rate limit for an IP.
     * Useful for testing or admin operations.
     */
    public void resetLimit(String ipAddress) {

        String key = KEY_PREFIX + ipAddress;

        redisTemplate.delete(key);

        logger.info("Rate limit reset for IP {}", ipAddress);
    }

    /**
     * Returns configured request limit.
     */
    public int getMaxRequests() {
        return maxRequests;
    }

    /**
     * Returns configured time window.
     */
    public int getWindowSeconds() {
        return windowSeconds;
    }
}