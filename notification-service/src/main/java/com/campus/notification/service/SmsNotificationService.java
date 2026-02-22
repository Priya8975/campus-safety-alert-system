package com.campus.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class SmsNotificationService {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationService.class);

    public ChannelResult send(UUID alertId, String title, String severity, long recipientCount) {
        long startTime = System.currentTimeMillis();

        // Simulate SMS delivery (mock Twilio)
        long sent = recipientCount;
        long delivered = (long) (recipientCount * 0.95); // 95% success rate
        long failed = sent - delivered;

        // Simulate processing time (100-500ms for SMS gateway)
        simulateLatency(100, 500);

        long latencyMs = System.currentTimeMillis() - startTime;

        log.info("SMS delivery complete [alertId={}, sent={}, delivered={}, failed={}, latencyMs={}]",
                alertId, sent, delivered, failed, latencyMs);

        return new ChannelResult("sms", sent, delivered, failed, latencyMs);
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
