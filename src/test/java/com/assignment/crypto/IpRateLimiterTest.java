package com.assignment.crypto;

import com.assignment.crypto.config.ratelimiter.IpRateLimiter;
import com.assignment.crypto.config.ratelimiter.RateLimiter;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IpRateLimiterTest {

    @Test
    public void testRateLimiting() {
        int rateLimit = 2;
        Duration resetDuration = Duration.ofMinutes(1);
        RateLimiter rateLimiter = new IpRateLimiter(rateLimit, resetDuration);

        String ip1 = "127.0.0.1";
        String ip2 = "192.168.0.1";

        assertTrue(rateLimiter.allowRequest(ip1));
        assertTrue(rateLimiter.allowRequest(ip1));

        assertFalse(rateLimiter.allowRequest(ip1)); // Exceeds rate limit for IP 1

        assertTrue(rateLimiter.allowRequest(ip2));
        assertTrue(rateLimiter.allowRequest(ip2));

        assertFalse(rateLimiter.allowRequest(ip2)); // Exceeds rate limit for IP 2
    }
}
