package com.campus.notification.service;

import com.campus.notification.kafka.DeliveryStatusEvent;
import com.campus.notification.kafka.DeliveryStatusProducer;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationFanoutService {

    private static final Logger log = LoggerFactory.getLogger(NotificationFanoutService.class);

    private final EmailNotificationService emailService;
    private final SmsNotificationService smsService;
    private final PushNotificationService pushService;
    private final DeliveryStatusProducer deliveryStatusProducer;

    public NotificationFanoutService(EmailNotificationService emailService,
                                      SmsNotificationService smsService,
                                      PushNotificationService pushService,
                                      DeliveryStatusProducer deliveryStatusProducer) {
        this.emailService = emailService;
        this.smsService = smsService;
        this.pushService = pushService;
        this.deliveryStatusProducer = deliveryStatusProducer;
    }

    public void fanOut(JsonNode enrichedEvent) {
        UUID alertId = UUID.fromString(enrichedEvent.get("alertId").asText());
        String title = enrichedEvent.get("title").asText();
        String severity = enrichedEvent.get("severity").asText();
        long recipientCount = enrichedEvent.get("recipientCount").asLong();

        List<String> channels = new ArrayList<>();
        enrichedEvent.get("channels").forEach(ch -> channels.add(ch.asText()));

        log.info("Starting notification fanout [alertId={}, channels={}, recipients={}]",
                alertId, channels, recipientCount);

        List<ChannelResult> results = new ArrayList<>();

        for (String channel : channels) {
            ChannelResult result = switch (channel) {
                case "email" -> emailService.send(alertId, title, severity, recipientCount);
                case "sms" -> smsService.send(alertId, title, severity, recipientCount);
                case "push" -> pushService.send(alertId, title, severity, recipientCount);
                default -> {
                    log.warn("Unknown channel: {}", channel);
                    yield null;
                }
            };

            if (result != null) {
                results.add(result);
                publishDeliveryStatus(alertId, result);
            }
        }

        long totalDelivered = results.stream().mapToLong(ChannelResult::getDelivered).sum();
        long totalFailed = results.stream().mapToLong(ChannelResult::getFailed).sum();
        long avgLatency = results.isEmpty() ? 0 :
                results.stream().mapToLong(ChannelResult::getLatencyMs).sum() / results.size();

        log.info("Notification fanout complete [alertId={}, channels={}, totalDelivered={}, totalFailed={}, avgLatencyMs={}]",
                alertId, channels.size(), totalDelivered, totalFailed, avgLatency);
    }

    private void publishDeliveryStatus(UUID alertId, ChannelResult result) {
        DeliveryStatusEvent status = new DeliveryStatusEvent();
        status.setAlertId(alertId);
        status.setChannel(result.getChannel());
        status.setTotalSent(result.getTotalSent());
        status.setDelivered(result.getDelivered());
        status.setFailed(result.getFailed());
        status.setAvgLatencyMs(result.getLatencyMs());

        deliveryStatusProducer.publish(status);
    }
}
