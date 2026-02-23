package com.campus.alertprocessing.kafka;

import com.campus.alertprocessing.service.AlertEnrichmentService;
import com.campus.alertprocessing.service.DeduplicationService;
import com.fasterxml.jackson.databind.JsonNode;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
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
    private final MeterRegistry meterRegistry;

    public AlertCreatedConsumer(DeduplicationService deduplicationService,
                                AlertEnrichmentService enrichmentService,
                                EnrichedEventProducer enrichedEventProducer,
                                MeterRegistry meterRegistry) {
        this.deduplicationService = deduplicationService;
        this.enrichmentService = enrichmentService;
        this.enrichedEventProducer = enrichedEventProducer;
        this.meterRegistry = meterRegistry;
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
            Counter.builder("alerts.duplicate.count")
                    .register(meterRegistry)
                    .increment();
            return;
        }

        // Step 2: Enrich the alert
        AlertEnrichedEvent enrichedEvent = enrichmentService.enrich(event);

        // Step 3: Publish enriched event
        enrichedEventProducer.publish(enrichedEvent);

        Counter.builder("alerts.enriched.count")
                .tag("severity", severity)
                .register(meterRegistry)
                .increment();
    }
}
