package com.campus.alertprocessing;

import com.campus.alertprocessing.model.SeverityLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SeverityLevelTest {

    @Test
    void shouldNotify_criticalAlertWithLowThreshold_shouldReturnTrue() {
        assertTrue(SeverityLevel.LOW.shouldNotify(SeverityLevel.CRITICAL));
    }

    @Test
    void shouldNotify_lowAlertWithHighThreshold_shouldReturnFalse() {
        assertFalse(SeverityLevel.HIGH.shouldNotify(SeverityLevel.LOW));
    }

    @Test
    void shouldNotify_sameLevel_shouldReturnTrue() {
        assertTrue(SeverityLevel.HIGH.shouldNotify(SeverityLevel.HIGH));
    }

    @Test
    void priority_shouldBeOrdered() {
        assertTrue(SeverityLevel.LOW.getPriority() < SeverityLevel.MEDIUM.getPriority());
        assertTrue(SeverityLevel.MEDIUM.getPriority() < SeverityLevel.HIGH.getPriority());
        assertTrue(SeverityLevel.HIGH.getPriority() < SeverityLevel.CRITICAL.getPriority());
    }
}
