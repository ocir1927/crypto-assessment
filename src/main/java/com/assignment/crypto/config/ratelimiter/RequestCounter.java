package com.assignment.crypto.config.ratelimiter;

import java.time.Instant;

public class RequestCounter {

    private int count = 0;
    private Instant lastRequestTime = Instant.MIN;

    public int getCount() {
        return count;
    }

    public void incrementCount() {
        count++;
        lastRequestTime = Instant.now();
    }

    public void removeExpiredRequests(Instant windowStart) {
        if (lastRequestTime.isBefore(windowStart)) {
            count = 0;
            lastRequestTime = Instant.MIN;
        }
    }
}
