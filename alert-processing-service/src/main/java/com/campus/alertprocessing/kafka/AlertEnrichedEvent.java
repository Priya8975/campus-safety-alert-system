package com.campus.alertprocessing.kafka;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class AlertEnrichedEvent {

    private UUID alertId;
    private String title;
    private String description;
    private String severity;
    private Double lat;
    private Double lng;
    private String campusZone;
    private List<String> affectedBuildings;
    private long recipientCount;
    private List<String> channels;
    private Instant timestamp;

    public AlertEnrichedEvent() {}

    public UUID getAlertId() { return alertId; }
    public void setAlertId(UUID alertId) { this.alertId = alertId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSeverity() { return severity; }
    public void setSeverity(String severity) { this.severity = severity; }

    public Double getLat() { return lat; }
    public void setLat(Double lat) { this.lat = lat; }

    public Double getLng() { return lng; }
    public void setLng(Double lng) { this.lng = lng; }

    public String getCampusZone() { return campusZone; }
    public void setCampusZone(String campusZone) { this.campusZone = campusZone; }

    public List<String> getAffectedBuildings() { return affectedBuildings; }
    public void setAffectedBuildings(List<String> affectedBuildings) { this.affectedBuildings = affectedBuildings; }

    public long getRecipientCount() { return recipientCount; }
    public void setRecipientCount(long recipientCount) { this.recipientCount = recipientCount; }

    public List<String> getChannels() { return channels; }
    public void setChannels(List<String> channels) { this.channels = channels; }

    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
}
