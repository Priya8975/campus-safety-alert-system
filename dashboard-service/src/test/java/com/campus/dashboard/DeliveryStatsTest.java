package com.campus.dashboard;

import com.campus.dashboard.graphql.dto.DeliveryStatsDto;
import com.campus.dashboard.graphql.dto.ChannelStatDto;
import com.campus.dashboard.model.DeliveryLog;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryStatsTest {

    @Test
    void fromLogs_shouldAggregateCorrectly() {
        UUID alertId = UUID.randomUUID();

        DeliveryLog emailLog = new DeliveryLog();
        emailLog.setAlertId(alertId);
        emailLog.setChannel("email");
        emailLog.setTotalSent(100);
        emailLog.setDelivered(97);
        emailLog.setFailed(3);
        emailLog.setAvgLatencyMs(150);

        DeliveryLog smsLog = new DeliveryLog();
        smsLog.setAlertId(alertId);
        smsLog.setChannel("sms");
        smsLog.setTotalSent(50);
        smsLog.setDelivered(47);
        smsLog.setFailed(3);
        smsLog.setAvgLatencyMs(300);

        DeliveryStatsDto stats = DeliveryStatsDto.fromLogs(alertId.toString(), List.of(emailLog, smsLog));

        assertEquals(150, stats.getTotalRecipients());
        assertEquals(144, stats.getDelivered());
        assertEquals(6, stats.getFailed());
        assertEquals(225, stats.getAvgLatencyMs()); // (150+300)/2
        assertEquals(2, stats.getByChannel().size());
    }

    @Test
    void fromLogs_singleChannel_shouldWork() {
        UUID alertId = UUID.randomUUID();

        DeliveryLog pushLog = new DeliveryLog();
        pushLog.setAlertId(alertId);
        pushLog.setChannel("push");
        pushLog.setTotalSent(200);
        pushLog.setDelivered(198);
        pushLog.setFailed(2);
        pushLog.setAvgLatencyMs(25);

        DeliveryStatsDto stats = DeliveryStatsDto.fromLogs(alertId.toString(), List.of(pushLog));

        assertEquals(200, stats.getTotalRecipients());
        assertEquals(198, stats.getDelivered());
        assertEquals(1, stats.getByChannel().size());
        assertEquals("push", stats.getByChannel().get(0).getChannel());
    }

    @Test
    void channelStatDto_shouldHoldValues() {
        ChannelStatDto stat = new ChannelStatDto("email", 100, 97, 3, 150);

        assertEquals("email", stat.getChannel());
        assertEquals(100, stat.getSent());
        assertEquals(97, stat.getDelivered());
        assertEquals(3, stat.getFailed());
        assertEquals(150, stat.getLatencyMs());
    }
}
