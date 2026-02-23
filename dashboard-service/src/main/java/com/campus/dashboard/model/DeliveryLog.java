package com.campus.dashboard.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "delivery_log")
public class DeliveryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "alert_id", nullable = false)
    private UUID alertId;

    @Column(nullable = false)
    private String channel;

    @Column(name = "total_sent")
    private int totalSent;

    private int delivered;

    private int failed;

    @Column(name = "avg_latency_ms")
    private long avgLatencyMs;

    @Column(name = "recorded_at")
    private Instant recordedAt;

    @PrePersist
    protected void onCreate() {
        recordedAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getAlertId() { return alertId; }
    public void setAlertId(UUID alertId) { this.alertId = alertId; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public int getTotalSent() { return totalSent; }
    public void setTotalSent(int totalSent) { this.totalSent = totalSent; }

    public int getDelivered() { return delivered; }
    public void setDelivered(int delivered) { this.delivered = delivered; }

    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }

    public long getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(long avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}
