package com.campus.alertprocessing.kafka;

import com.campus.alertprocessing.service.AlertEnrichmentService;
import com.campus.alertprocessing.service.DeduplicationService;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class AlertCreatedConsumer {

    private static final Logger log = LoggerFactory.getLogger(AlertCreatedConsumer.class);

    private final DeduplicationService deduplicationService;
    private final AlertEnrichmentService enrichmentService;
    private final EnrichedEventProducer enrichedEventProducer;

    public AlertCreatedConsumer(DeduplicationService deduplicationService,
                                AlertEnrichmentService enrichmentService,
                                EnrichedEventProducer enrichedEventProducer) {
        this.deduplicationService = deduplicationService;
        this.enrichmentService = enrichmentService;
        this.enrichedEventProducer = enrichedEventProducer;
    }

    @KafkaListener(topics = "alert-created", groupId = "alert-processing-group")
    public void consume(JsonNode event) {
        String alertId = event.get("alertId").asText();
        String severity = event.get("severity").asText();
        String title = event.get("title").asText();
        String zone = event.has("campusZone") && !event.get("campusZone").isNull()
                ? event.get("campusZone").asText() : null;

        log.info("Received alert-created event [alertId={}, severity={}, title={}]",
                alertId, severity, title);

        // Step 1: Deduplication check
        if (deduplicationService.isDuplicate(severity, zone, title)) {
            log.warn("Duplicate alert rejected [alertId={}, title={}]", alertId, title);
            return;
        }

        // Step 2: Enrich the alert
        AlertEnrichedEvent enrichedEvent = enrichmentService.enrich(event);

        // Step 3: Publish enriched event
        enrichedEventProducer.publish(enrichedEvent);
    }
}
