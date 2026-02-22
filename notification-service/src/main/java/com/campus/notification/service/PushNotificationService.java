package com.campus.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class PushNotificationService {

    private static final Logger log = LoggerFactory.getLogger(PushNotificationService.class);

    public ChannelResult send(UUID alertId, String title, String severity, long recipientCount) {
        long startTime = System.currentTimeMillis();

        // Simulate push notification delivery (WebSocket/FCM)
        long sent = recipientCount;
        long delivered = (long) (recipientCount * 0.99); // 99% success for push
        long failed = sent - delivered;

        // Simulate processing time (10-50ms for push - fastest channel)
        simulateLatency(10, 50);

        long latencyMs = System.currentTimeMillis() - startTime;

        log.info("Push delivery complete [alertId={}, sent={}, delivered={}, failed={}, latencyMs={}]",
                alertId, sent, delivered, failed, latencyMs);

        return new ChannelResult("push", sent, delivered, failed, latencyMs);
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
