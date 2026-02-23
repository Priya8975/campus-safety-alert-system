package com.campus.dashboard.service;

import com.campus.dashboard.model.DeliveryLog;
import com.campus.dashboard.repository.DeliveryLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class DeliveryStatsService {

    private final DeliveryLogRepository deliveryLogRepository;

    public DeliveryStatsService(DeliveryLogRepository deliveryLogRepository) {
        this.deliveryLogRepository = deliveryLogRepository;
    }

    public List<DeliveryLog> getDeliveryLogs(UUID alertId) {
        return deliveryLogRepository.findByAlertId(alertId);
    }

    public void recordDelivery(UUID alertId, String channel, int totalSent,
                                int delivered, int failed, long avgLatencyMs) {
        DeliveryLog log = new DeliveryLog();
        log.setAlertId(alertId);
        log.setChannel(channel);
        log.setTotalSent(totalSent);
        log.setDelivered(delivered);
        log.setFailed(failed);
        log.setAvgLatencyMs(avgLatencyMs);
        deliveryLogRepository.save(log);
    }
}
