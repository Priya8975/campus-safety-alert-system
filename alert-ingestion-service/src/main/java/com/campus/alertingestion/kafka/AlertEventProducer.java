package com.campus.alertingestion.kafka;

import com.campus.alertingestion.dto.AlertEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AlertEventProducer {

    private static final Logger log = LoggerFactory.getLogger(AlertEventProducer.class);
    private static final String TOPIC = "alert-created";

    private final KafkaTemplate<String, AlertEvent> kafkaTemplate;

    public AlertEventProducer(KafkaTemplate<String, AlertEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(AlertEvent event) {
        String key = event.getAlertId().toString();

        CompletableFuture<SendResult<String, AlertEvent>> future =
                kafkaTemplate.send(TOPIC, key, event);

        future.whenComplete((result, ex) -> {
            if (ex != null) {
                log.error("Failed to publish alert event [alertId={}]: {}",
                        event.getAlertId(), ex.getMessage());
            } else {
                log.info("Published alert event [alertId={}, topic={}, partition={}, offset={}]",
                        event.getAlertId(),
                        result.getRecordMetadata().topic(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset());
            }
        });
    }
}
