package com.campus.alertingestion.dto;

import com.campus.alertingestion.model.Severity;

import java.time.Instant;
import java.util.UUID;

public class AlertEvent {

    private UUID alertId;
    private String title;
    private String description;
    private Severity severity;
    private Double lat;
    private Double lng;
    private String campusZone;
    private String source;
    private Instant timestamp;

    public AlertEvent() {}

    public AlertEvent(UUID alertId, String title, String description, Severity severity,
                      Double lat, Double lng, String campusZone, String source, Instant timestamp) {
        this.alertId = alertId;
        this.title = title;
        this.description = description;
        this.severity = severity;
        this.lat = lat;
        this.lng = lng;
        this.campusZone = campusZone;
        this.source = source;
        this.timestamp = timestamp;
    }

    public UUID getAlertId() { return alertId; }
    public void setAlertId(UUID alertId) { this.alertId = alertId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Severity getSeverity() { return severity; }
    public void setSeverity(Severity severity) { this.severity = severity; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
