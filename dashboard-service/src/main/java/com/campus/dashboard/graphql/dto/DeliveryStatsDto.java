package com.campus.dashboard.graphql.dto;

import com.campus.dashboard.model.DeliveryLog;

import java.util.List;
import java.util.stream.Collectors;

public class DeliveryStatsDto {

    private String alertId;
    private int totalRecipients;
    private int delivered;
    private int failed;
    private int avgLatencyMs;
    private List<ChannelStatDto> byChannel;

    public DeliveryStatsDto() {}

    public DeliveryStatsDto(String alertId, int totalRecipients, int delivered,
                            int failed, int avgLatencyMs, List<ChannelStatDto> byChannel) {
        this.alertId = alertId;
        this.totalRecipients = totalRecipients;
        this.delivered = delivered;
        this.failed = failed;
        this.avgLatencyMs = avgLatencyMs;
        this.byChannel = byChannel;
    }

    public static DeliveryStatsDto fromLogs(String alertId, List<DeliveryLog> logs) {
        List<ChannelStatDto> channels = logs.stream()
                .map(log -> new ChannelStatDto(
                        log.getChannel(),
                        log.getTotalSent(),
                        log.getDelivered(),
                        log.getFailed(),
                        (int) log.getAvgLatencyMs()))
                .collect(Collectors.toList());

        int totalRecipients = logs.stream().mapToInt(DeliveryLog::getTotalSent).sum();
        int totalDelivered = logs.stream().mapToInt(DeliveryLog::getDelivered).sum();
        int totalFailed = logs.stream().mapToInt(DeliveryLog::getFailed).sum();
        int avgLatency = logs.isEmpty() ? 0 :
                (int) logs.stream().mapToLong(DeliveryLog::getAvgLatencyMs).average().orElse(0);

        return new DeliveryStatsDto(alertId, totalRecipients, totalDelivered,
                totalFailed, avgLatency, channels);
    }

    public String getAlertId() { return alertId; }
    public void setAlertId(String alertId) { this.alertId = alertId; }

    public int getTotalRecipients() { return totalRecipients; }
    public void setTotalRecipients(int totalRecipients) { this.totalRecipients = totalRecipients; }

    public int getDelivered() { return delivered; }
    public void setDelivered(int delivered) { this.delivered = delivered; }

    public int getFailed() { return failed; }
    public void setFailed(int failed) { this.failed = failed; }

    public int getAvgLatencyMs() { return avgLatencyMs; }
    public void setAvgLatencyMs(int avgLatencyMs) { this.avgLatencyMs = avgLatencyMs; }

    public List<ChannelStatDto> getByChannel() { return byChannel; }
    public void setByChannel(List<ChannelStatDto> byChannel) { this.byChannel = byChannel; }
}
