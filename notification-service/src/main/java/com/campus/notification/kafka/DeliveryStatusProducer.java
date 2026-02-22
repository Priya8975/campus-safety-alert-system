package com.campus.notification.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryStatusProducer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryStatusProducer.class);
    private static final String TOPIC = "alert-delivery-status";

    private final KafkaTemplate<String, DeliveryStatusEvent> kafkaTemplate;

    public DeliveryStatusProducer(KafkaTemplate<String, DeliveryStatusEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publish(DeliveryStatusEvent event) {
        String key = event.getAlertId().toString();
        kafkaTemplate.send(TOPIC, key, event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Failed to publish delivery status [alertId={}, channel={}]: {}",
                                event.getAlertId(), event.getChannel(), ex.getMessage());
                    } else {
                        log.info("Published delivery status [alertId={}, channel={}, delivered={}/{}]",
                                event.getAlertId(), event.getChannel(),
                                event.getDelivered(), event.getTotalSent());
                    }
                });
    }
}
