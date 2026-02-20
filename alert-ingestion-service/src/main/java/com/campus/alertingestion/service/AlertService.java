package com.campus.alertingestion.service;

import com.campus.alertingestion.dto.AlertEvent;
import com.campus.alertingestion.dto.CreateAlertRequest;
import com.campus.alertingestion.kafka.AlertEventProducer;
import com.campus.alertingestion.model.Alert;
import com.campus.alertingestion.model.AlertStatus;
import com.campus.alertingestion.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;
    private final AlertEventProducer alertEventProducer;

    public AlertService(AlertRepository alertRepository, AlertEventProducer alertEventProducer) {
        this.alertRepository = alertRepository;
        this.alertEventProducer = alertEventProducer;
    }

    @Transactional
    public Alert createAlert(CreateAlertRequest request) {
        Alert alert = new Alert();
        alert.setTitle(request.getTitle());
        alert.setDescription(request.getDescription());
        alert.setSeverity(request.getSeverity());
        alert.setLat(request.getLat());
        alert.setLng(request.getLng());
        alert.setCampusZone(request.getCampusZone());
        alert.setStatus(AlertStatus.ACTIVE);

        Alert saved = alertRepository.save(alert);
        log.info("Alert persisted [id={}, title={}, severity={}]",
                saved.getId(), saved.getTitle(), saved.getSeverity());

        AlertEvent event = new AlertEvent(
                saved.getId(),
                saved.getTitle(),
                saved.getDescription(),
                saved.getSeverity(),
                saved.getLat(),
                saved.getLng(),
                saved.getCampusZone(),
                "campus_police",
                Instant.now()
        );

        alertEventProducer.publish(event);

        return saved;
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.ACTIVE);
    }

    public Alert getAlert(UUID id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
    }
}
