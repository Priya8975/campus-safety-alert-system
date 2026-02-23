package com.campus.dashboard.kafka;

import com.campus.dashboard.model.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class AlertEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertEventProducer.class);
    private static final String TOPIC = "alert-created";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public AlertEventProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(Alert alert) {
        AlertCreatedEvent event = new AlertCreatedEvent();
        event.setAlertId(alert.getId().toString());
        event.setTitle(alert.getTitle());
        event.setDescription(alert.getDescription());
        event.setSeverity(alert.getSeverity().name());
        event.setLat(alert.getLat());
        event.setLng(alert.getLng());
        event.setCampusZone(alert.getCampusZone());
        event.setCreatedAt(alert.getCreatedAt().toString());

        kafkaTemplate.send(TOPIC, alert.getId().toString(), event);
        log.info("Published alert-created event from dashboard [alertId={}]", alert.getId());
    }
}
