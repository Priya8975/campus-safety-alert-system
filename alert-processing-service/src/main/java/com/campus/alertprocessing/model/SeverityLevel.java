package com.campus.alertprocessing.model;

public enum SeverityLevel {
    LOW(1),
    MEDIUM(2),
    HIGH(3),
    CRITICAL(4);

    private final int priority;

    SeverityLevel(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public boolean shouldNotify(SeverityLevel alertSeverity) {
        return alertSeverity.priority >= this.priority;
    }
}
