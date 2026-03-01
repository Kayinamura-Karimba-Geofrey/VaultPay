package com.vaultpay.security.ratelimit;

import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimitService {

    private static class Bucket {
        int tokens;
        long lastRefill;
    }

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private static final int MAX_TOKENS = 5;      // limit
    private static final int REFILL_SECONDS = 60; // time window

    public boolean allowRequest(String key) {

        Bucket bucket = buckets.computeIfAbsent(key, k -> {
            Bucket b = new Bucket();
            b.tokens = MAX_TOKENS;
            b.lastRefill = Instant.now().getEpochSecond();
            return b;
        });

        refill(bucket);

        if (bucket.tokens > 0) {
            bucket.tokens--;
            return true;
        }

        return false;
    }

    private void refill(Bucket bucket) {
        long now = Instant.now().getEpochSecond();
        long secondsPassed = now - bucket.lastRefill;

        if (secondsPassed >= REFILL_SECONDS) {
            bucket.tokens = MAX_TOKENS;
            bucket.lastRefill = now;
        }
    }
}
