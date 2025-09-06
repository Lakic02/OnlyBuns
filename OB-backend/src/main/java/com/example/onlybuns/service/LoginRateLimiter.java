package com.example.onlybuns.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LoginRateLimiter {

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        // Dozvoljeno 5 pokušaja po minuti
        Refill refill = Refill.greedy(5, Duration.ofMinutes(1));
        Bandwidth limit = Bandwidth.classic(5, refill);
        return Bucket4j.builder()
                .addLimit(limit)
                .build();
    }

                
    

    public boolean tryConsume(String ip) {
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createNewBucket());
        return bucket.tryConsume(1);
    }
}
