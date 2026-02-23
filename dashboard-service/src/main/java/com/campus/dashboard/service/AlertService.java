package com.campus.dashboard.service;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.AlertStatus;
import com.campus.dashboard.model.Severity;
import com.campus.dashboard.repository.AlertRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AlertService {

    private static final Logger log = LoggerFactory.getLogger(AlertService.class);

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> getActiveAlerts() {
        return alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.ACTIVE);
    }

    public Optional<Alert> getAlert(UUID id) {
        return alertRepository.findById(id);
    }

    public List<Alert> getAlertHistory(String zone, Severity severity, Integer limit) {
        List<Alert> results = alertRepository.findAlertHistory(zone, severity);
        if (limit != null && limit > 0 && results.size() > limit) {
            return results.subList(0, limit);
        }
        return results;
    }

    @Transactional
    public Alert createAlert(String title, String description, Severity severity,
                             double lat, double lng, String campusZone) {
        Alert alert = new Alert();
        alert.setTitle(title);
        alert.setDescription(description);
        alert.setSeverity(severity);
        alert.setLat(lat);
        alert.setLng(lng);
        alert.setCampusZone(campusZone);
        alert.setStatus(AlertStatus.ACTIVE);

        Alert saved = alertRepository.save(alert);
        log.info("Alert created via GraphQL [id={}, severity={}, zone={}]",
                saved.getId(), severity, campusZone);
        return saved;
    }

    @Transactional
    public Alert acknowledgeAlert(UUID id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        log.info("Alert acknowledged [id={}]", id);
        return alertRepository.save(alert);
    }

    @Transactional
    public Alert resolveAlert(UUID id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Alert not found: " + id));
        alert.setStatus(AlertStatus.RESOLVED);
        log.info("Alert resolved [id={}]", id);
        return alertRepository.save(alert);
    }
}
