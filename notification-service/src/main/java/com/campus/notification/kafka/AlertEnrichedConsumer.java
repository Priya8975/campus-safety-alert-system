package com.campus.notification.kafka;

import com.campus.notification.service.NotificationFanoutService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertEnrichedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertEnrichedConsumer.class);

    private final NotificationFanoutService fanoutService;

    public AlertEnrichedConsumer(NotificationFanoutService fanoutService) {
        this.fanoutService = fanoutService;
    }

    @KafkaListener(topics = "alert-enriched", groupId = "notification-group")
    public void consume(JsonNode event) {
        String alertId = event.get("alertId").asText();
        String severity = event.get("severity").asText();

        log.info("Received alert-enriched event [alertId={}, severity={}]", alertId, severity);

        fanoutService.fanOut(event);
    }
}
