package com.assignment.crypto.config.ratelimiter;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class IpRateLimiter implements RateLimiter{

    private final Map<String, RequestCounter> requestCounts = new ConcurrentHashMap<>();
    private final int rateLimit;
    private final Duration resetDuration;

    public IpRateLimiter(int rateLimit, Duration resetDuration) {
        this.rateLimit = rateLimit;
        this.resetDuration = resetDuration;
    }

    @Override
    public boolean allowRequest(String ipAddress) {
        RequestCounter requestCounter = requestCounts.computeIfAbsent(ipAddress, key -> new RequestCounter());

        Instant now = Instant.now();
        Instant windowStart = now.minus(resetDuration);

        requestCounter.removeExpiredRequests(windowStart);

        if (requestCounter.getCount() >= rateLimit) {
            return false;
        }

        requestCounter.incrementCount();
        return true;
    }
}
