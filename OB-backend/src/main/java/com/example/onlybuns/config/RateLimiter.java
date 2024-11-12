package com.example.onlybuns.config;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ConcurrentHashMap;

public class RateLimiter {
    private final int maxRequestsPerMinute;
    private final ConcurrentHashMap<Long, UserRequestData> requestCounts = new ConcurrentHashMap<>();

    public RateLimiter(int maxRequestsPerMinute) {
        this.maxRequestsPerMinute = maxRequestsPerMinute;
    }

    public boolean isAllowed(Long userId) {
        LocalDateTime now = LocalDateTime.now();
    
        requestCounts.compute(userId, (id, userData) -> {
            if (userData == null) {
                return new UserRequestData(1, now); 
            } else if (userData.timestamp.isBefore(now.minusMinutes(1))) {
                return new UserRequestData(1, now); 
            } else if (userData.requestCount <= maxRequestsPerMinute) {
                userData.requestCount++;
                return userData;
            }

            return userData;
        });
    
        int currentRequestCount = requestCounts.get(userId).requestCount;
    
        boolean allowed = currentRequestCount <= maxRequestsPerMinute; 
       
    
        return allowed; 
    }

    private static class UserRequestData {
        int requestCount;
        LocalDateTime timestamp;

        UserRequestData(int requestCount, LocalDateTime timestamp) {
            this.requestCount = requestCount;
            this.timestamp = timestamp;
        }
    }
}
