package com.campus.notification;

import com.campus.notification.service.EmailNotificationService;
import com.campus.notification.service.PushNotificationService;
import com.campus.notification.service.SmsNotificationService;
import com.campus.notification.service.ChannelResult;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class NotificationFanoutServiceTest {

    @Test
    void emailService_shouldReturnValidResult() {
        EmailNotificationService emailService = new EmailNotificationService();
        UUID alertId = UUID.randomUUID();

        ChannelResult result = emailService.send(alertId, "Test alert", "HIGH", 100);

        assertEquals("email", result.getChannel());
        assertEquals(100, result.getTotalSent());
        assertTrue(result.getDelivered() > 0);
        assertTrue(result.getDelivered() <= result.getTotalSent());
        assertEquals(result.getTotalSent(), result.getDelivered() + result.getFailed());
        assertTrue(result.getLatencyMs() > 0);
    }

    @Test
    void smsService_shouldReturnValidResult() {
        SmsNotificationService smsService = new SmsNotificationService();
        UUID alertId = UUID.randomUUID();

        ChannelResult result = smsService.send(alertId, "Test alert", "CRITICAL", 50);

        assertEquals("sms", result.getChannel());
        assertEquals(50, result.getTotalSent());
        assertTrue(result.getDelivered() > 0);
    }

    @Test
    void pushService_shouldReturnValidResult() {
        PushNotificationService pushService = new PushNotificationService();
        UUID alertId = UUID.randomUUID();

        ChannelResult result = pushService.send(alertId, "Test alert", "HIGH", 200);

        assertEquals("push", result.getChannel());
        assertEquals(200, result.getTotalSent());
        assertTrue(result.getDelivered() >= result.getTotalSent() * 0.95);
    }

    @Test
    void pushService_shouldBeFasterThanSms() {
        PushNotificationService pushService = new PushNotificationService();
        SmsNotificationService smsService = new SmsNotificationService();
        UUID alertId = UUID.randomUUID();

        ChannelResult pushResult = pushService.send(alertId, "Test", "HIGH", 100);
        ChannelResult smsResult = smsService.send(alertId, "Test", "HIGH", 100);

        assertTrue(pushResult.getLatencyMs() < smsResult.getLatencyMs(),
                "Push should be faster than SMS");
    }
}
