package com.campus.alertingestion.dto;

import com.campus.alertingestion.model.Alert;
import com.campus.alertingestion.model.AlertStatus;
import com.campus.alertingestion.model.Severity;

import java.time.Instant;
import java.util.UUID;

public class AlertResponse {

    private UUID id;
    private String title;
    private String description;
    private Severity severity;
    private Double lat;
    private Double lng;
    private String campusZone;
    private AlertStatus status;
    private Instant createdAt;

    public static AlertResponse from(Alert alert) {
        AlertResponse response = new AlertResponse();
        response.id = alert.getId();
        response.title = alert.getTitle();
        response.description = alert.getDescription();
        response.severity = alert.getSeverity();
        response.lat = alert.getLat();
        response.lng = alert.getLng();
        response.campusZone = alert.getCampusZone();
        response.status = alert.getStatus();
        response.createdAt = alert.getCreatedAt();
        return response;
    }

    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Severity getSeverity() { return severity; }
    public Double getLat() { return lat; }
    public Double getLng() { return lng; }
    public String getCampusZone() { return campusZone; }
    public AlertStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }
}
