package com.campus.dashboard.kafka;

import com.campus.dashboard.graphql.AlertSubscriptionController;
import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.AlertStatus;
import com.campus.dashboard.model.Severity;
import com.campus.dashboard.repository.AlertRepository;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

@Component
public class AlertCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertCreatedConsumer.class);

    private final AlertRepository alertRepository;
    private final AlertSubscriptionController subscriptionController;

    public AlertCreatedConsumer(AlertRepository alertRepository,
                                AlertSubscriptionController subscriptionController) {
        this.alertRepository = alertRepository;
        this.subscriptionController = subscriptionController;
    }

    @KafkaListener(topics = "alert-created", groupId = "dashboard-group")
    public void consume(JsonNode event) {
        String alertId = event.has("alertId") ? event.get("alertId").asText() :
                         event.has("id") ? event.get("id").asText() : null;

        if (alertId == null) {
            log.warn("Received alert-created event without ID, skipping");
            return;
        }

        log.info("Received alert-created event [alertId={}]", alertId);

        // Check if alert already exists (created via GraphQL mutation)
        Optional<Alert> existing = alertRepository.findById(UUID.fromString(alertId));
        Alert alert;

        if (existing.isPresent()) {
            alert = existing.get();
        } else {
            // Alert came from ingestion service, sync to local DB
            alert = new Alert();
            alert.setId(UUID.fromString(alertId));
            alert.setTitle(event.has("title") ? event.get("title").asText() : "Unknown");
            alert.setDescription(event.has("description") ? event.get("description").asText() : null);
            alert.setSeverity(Severity.valueOf(event.get("severity").asText()));
            alert.setLat(event.get("lat").asDouble());
            alert.setLng(event.get("lng").asDouble());
            alert.setCampusZone(event.has("campusZone") ? event.get("campusZone").asText() : null);
            alert.setStatus(AlertStatus.ACTIVE);
            alert.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            alert.setUpdatedAt(OffsetDateTime.now(ZoneOffset.UTC));
            alert = alertRepository.save(alert);
            log.info("Synced alert from ingestion service [id={}]", alertId);
        }

        // Push to GraphQL subscription
        subscriptionController.publishAlert(alert);
    }
}
