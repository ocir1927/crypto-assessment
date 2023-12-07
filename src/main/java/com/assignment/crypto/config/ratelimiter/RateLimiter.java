package com.assignment.crypto.config.ratelimiter;

public interface RateLimiter {
    boolean allowRequest(String ipAddress);
}
