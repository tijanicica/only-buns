package com.project.onlybuns.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@AllArgsConstructor
@Component
public class IpTrackingService {

    private final Map<String, AttemptInfo> attemptsByIp = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 3;
    private static final long BLOCK_TIME = 60 * 1000L;


    private static class AttemptInfo {
        int attempts;
        long lastAttemptTime;

        public AttemptInfo(int attempts, long lastAttemptTime) {
            this.attempts = attempts;
            this.lastAttemptTime = lastAttemptTime;
        }
    }

    public void trackAttempt(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        long currentTime = Instant.now().toEpochMilli();


        attemptsByIp.compute(ipAddress, (ip, attemptInfo) -> {
            if (attemptInfo == null || currentTime - attemptInfo.lastAttemptTime > BLOCK_TIME) {
                return new AttemptInfo(1, currentTime);
            } else {

                return new AttemptInfo(attemptInfo.attempts + 1, currentTime);
            }
        });
    }

    public void resetAttempts(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        attemptsByIp.remove(ipAddress);
    }

    public boolean isBlocked(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        AttemptInfo attemptInfo = attemptsByIp.get(ipAddress);

        if (attemptInfo == null) {
            return false;
        }


        if (Instant.now().toEpochMilli() - attemptInfo.lastAttemptTime > BLOCK_TIME) {
            resetAttempts(request);
            return false;
        }
        return attemptInfo.attempts >= MAX_ATTEMPTS;
    }
}

