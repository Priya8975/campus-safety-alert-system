package com.campus.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class EmailNotificationService {

    private static final Logger log = LoggerFactory.getLogger(EmailNotificationService.class);

    public ChannelResult send(UUID alertId, String title, String severity, long recipientCount) {
        long startTime = System.currentTimeMillis();

        // Simulate email delivery with realistic latency
        long sent = recipientCount;
        long delivered = (long) (recipientCount * 0.97); // 97% success rate
        long failed = sent - delivered;

        // Simulate processing time (50-200ms for batch email)
        simulateLatency(50, 200);

        long latencyMs = System.currentTimeMillis() - startTime;

        log.info("Email delivery complete [alertId={}, sent={}, delivered={}, failed={}, latencyMs={}]",
                alertId, sent, delivered, failed, latencyMs);

        return new ChannelResult("email", sent, delivered, failed, latencyMs);
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
