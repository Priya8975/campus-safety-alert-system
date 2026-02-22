package com.campus.notification.service;

public class ChannelResult {

    private final String channel;
    private final long totalSent;
    private final long delivered;
    private final long failed;
    private final long latencyMs;

    public ChannelResult(String channel, long totalSent, long delivered, long failed, long latencyMs) {
        this.channel = channel;
        this.totalSent = totalSent;
        this.delivered = delivered;
        this.failed = failed;
        this.latencyMs = latencyMs;
    }

    public String getChannel() { return channel; }
    public long getTotalSent() { return totalSent; }
    public long getDelivered() { return delivered; }
    public long getFailed() { return failed; }
    public long getLatencyMs() { return latencyMs; }
}
