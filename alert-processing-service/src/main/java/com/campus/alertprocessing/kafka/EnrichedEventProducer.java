package com.campus.alertprocessing.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class EnrichedEventProducer {

    private static final Logger log = LoggerFactory.getLogger(EnrichedEventProducer.class);
    private static final String TOPIC = "alert-enriched";

    private final KafkaTemplate<String, AlertEnrichedEvent> kafkaTemplate;

    public EnrichedEventProducer(KafkaTemplate<String, AlertEnrichedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AlertEnrichedEvent event) {
        String key = event.getAlertId().toString();
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish enriched event [alertId={}]: {}",
                                event.getAlertId(), ex.getMessage());
                    } else {
                        log.info("Published enriched event [alertId={}, buildings={}, recipients={}]",
                                event.getAlertId(),
                                event.getAffectedBuildings().size(),
                                event.getRecipientCount());
                    }
                });
    }
}
