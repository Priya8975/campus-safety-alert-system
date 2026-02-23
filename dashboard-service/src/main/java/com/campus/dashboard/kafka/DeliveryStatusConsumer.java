package com.campus.dashboard.kafka;

import com.campus.dashboard.graphql.AlertSubscriptionController;
import com.campus.dashboard.graphql.dto.DeliveryStatsDto;
import com.campus.dashboard.graphql.dto.ChannelStatDto;
import com.campus.dashboard.service.DeliveryStatsService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class DeliveryStatusConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryStatusConsumer.class);

    private final DeliveryStatsService deliveryStatsService;
    private final AlertSubscriptionController subscriptionController;

    public DeliveryStatusConsumer(DeliveryStatsService deliveryStatsService,
                                  AlertSubscriptionController subscriptionController) {
        this.deliveryStatsService = deliveryStatsService;
        this.subscriptionController = subscriptionController;
    }

    @KafkaListener(topics = "alert-delivery-status", groupId = "dashboard-group")
    public void consume(JsonNode event) {
        String alertId = event.get("alertId").asText();
        String channel = event.get("channel").asText();
        int totalSent = event.get("totalSent").asInt();
        int delivered = event.get("delivered").asInt();
        int failed = event.get("failed").asInt();
        long avgLatencyMs = event.get("avgLatencyMs").asLong();

        log.info("Received delivery status [alertId={}, channel={}, delivered={}/{}]",
                alertId, channel, delivered, totalSent);

        // Persist delivery log
        deliveryStatsService.recordDelivery(
                UUID.fromString(alertId), channel, totalSent, delivered, failed, avgLatencyMs);

        // Push real-time update to GraphQL subscription
        ChannelStatDto channelStat = new ChannelStatDto(channel, totalSent, delivered, failed, (int) avgLatencyMs);
        DeliveryStatsDto stats = new DeliveryStatsDto(
                alertId, totalSent, delivered, failed, (int) avgLatencyMs, List.of(channelStat));
        subscriptionController.publishDeliveryUpdate(stats);
    }
}
