package com.campus.alertprocessing;

import com.campus.alertprocessing.service.DeduplicationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeduplicationServiceTest {

    @Mock
    private StringRedisTemplate redisTemplate;

    @Mock
    private ValueOperations<String, String> valueOperations;

    private DeduplicationService deduplicationService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        deduplicationService = new DeduplicationService(redisTemplate);
    }

    @Test
    void isDuplicate_newAlert_shouldReturnFalse() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        boolean result = deduplicationService.isDuplicate("HIGH", "SAC", "Suspicious activity");

        assertFalse(result);
    }

    @Test
    void isDuplicate_existingAlert_shouldReturnTrue() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(false);

        boolean result = deduplicationService.isDuplicate("HIGH", "SAC", "Suspicious activity");

        assertTrue(result);
    }

    @Test
    void isDuplicate_differentAlerts_shouldNotConflict() {
        when(valueOperations.setIfAbsent(anyString(), anyString(), any(Duration.class)))
                .thenReturn(true);

        boolean result1 = deduplicationService.isDuplicate("HIGH", "SAC", "Fire alarm");
        boolean result2 = deduplicationService.isDuplicate("LOW", "Library", "Power outage");

        assertFalse(result1);
        assertFalse(result2);
    }
}
