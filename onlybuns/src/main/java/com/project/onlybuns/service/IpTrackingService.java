package com.project.onlybuns.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@AllArgsConstructor
@Component
public class IpTrackingService {

    private final Map<String, AttemptInfo> attemptsByIp = new ConcurrentHashMap<>();
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_TIME = TimeUnit.MINUTES.toMillis(1); //nakon 1 min se resetuje

    //ciscenje konkurent mape
    private static final long EXPIRATION_TIME_MS = TimeUnit.MINUTES.toMillis(5);
    private static final Logger logger = LoggerFactory.getLogger(IpTrackingService.class);

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
            return false;
        }
        return attemptInfo.attempts >= MAX_ATTEMPTS;
    }

    // 300 000ms je 5min
    // mapa se cisti na svakih 5 min
    @Scheduled(fixedRate = 300000L)
    public void cleanUpExpiredAttempts() {
        long currentTime = Instant.now().toEpochMilli();
        logger.info("Running scheduled cleanup for expired IP attempts. Current map size: {}", attemptsByIp.size());
        attemptsByIp.entrySet().removeIf(
                entry -> (currentTime - entry.getValue().lastAttemptTime) > EXPIRATION_TIME_MS
        );

        logger.info("Finished cleanup. New map size: {}", attemptsByIp.size());
    }
}

