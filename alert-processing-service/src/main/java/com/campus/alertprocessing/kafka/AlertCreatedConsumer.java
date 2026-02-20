package com.campus.alertprocessing.kafka;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertCreatedConsumer.class);

    @KafkaListener(topics = "alert-created", groupId = "alert-processing-group")
    public void consume(JsonNode event) {
        log.info("Received alert-created event: alertId={}, title={}, severity={}",
                event.get("alertId"),
                event.get("title"),
                event.get("severity"));
    }
}
