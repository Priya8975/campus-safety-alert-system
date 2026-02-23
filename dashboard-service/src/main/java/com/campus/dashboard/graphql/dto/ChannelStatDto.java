package com.campus.dashboard.graphql.dto;

public class ChannelStatDto {

    private final String channel;
    private final int sent;
    private final int delivered;
    private final int failed;
    private final int latencyMs;

    public ChannelStatDto(String channel, int sent, int delivered, int failed, int latencyMs) {
        this.channel = channel;
        this.sent = sent;
        this.delivered = delivered;
        this.failed = failed;
        this.latencyMs = latencyMs;
    }

    public String getChannel() { return channel; }
    public int getSent() { return sent; }
    public int getDelivered() { return delivered; }
    public int getFailed() { return failed; }
    public int getLatencyMs() { return latencyMs; }
}
