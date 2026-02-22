package com.campus.alertprocessing.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class DeduplicationService {

    private static final Logger log = LoggerFactory.getLogger(DeduplicationService.class);
    private static final String DEDUP_KEY_PREFIX = "alert:dedup:";
    private static final Duration DEDUP_WINDOW = Duration.ofMinutes(5);

    private final StringRedisTemplate redisTemplate;

    public DeduplicationService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Checks if an alert is a duplicate within the 5-minute sliding window.
     * Uses Redis SETNX (Set if Not Exists) for atomic deduplication.
     *
     * @param severity the alert severity
     * @param zone the campus zone
     * @param title the alert title
     * @return true if this is a duplicate alert, false if it's new
     */
    public boolean isDuplicate(String severity, String zone, String title) {
        String hash = generateHash(severity, zone, title);
        String key = DEDUP_KEY_PREFIX + hash;

        Boolean wasSet = redisTemplate.opsForValue().setIfAbsent(key, "1", DEDUP_WINDOW);

        if (Boolean.TRUE.equals(wasSet)) {
            log.debug("New alert accepted [hash={}]", hash);
            return false;
        } else {
            log.info("Duplicate alert rejected [hash={}, severity={}, zone={}]",
                    hash, severity, zone);
            return true;
        }
    }

    private String generateHash(String severity, String zone, String title) {
        String normalized = (severity + ":" + (zone != null ? zone : "") + ":" + title)
                .toLowerCase().trim();
        return Integer.toHexString(normalized.hashCode());
    }
}
