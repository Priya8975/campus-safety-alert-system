package com.campus.notification.kafka;

import java.util.UUID;

public class DeliveryStatusEvent {

    private UUID alertId;
    private String channel;
    private long totalSent;
    private long delivered;
    private long failed;
    private long avgLatencyMs;

    public DeliveryStatusEvent() {}

    public UUID getAlertId() { return alertId; }
    public void setAlertId(UUID alertId) { this.alertId = alertId; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public long getTotalSent() { return totalSent; }
    public void setTotalSent(long totalSent) { this.totalSent = totalSent; }

    public long getDelivered() { return delivered; }
    public void setDelivered(long delivered) { this.delivered = delivered; }

    public long getFailed() { return failed; }
    public void setFailed(long failed) { this.failed = failed; }

    public long getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(long avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }
}
