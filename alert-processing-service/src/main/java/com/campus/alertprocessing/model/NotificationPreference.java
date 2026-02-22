package com.campus.alertprocessing.model;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "notification_preferences")
public class NotificationPreference {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String channel;

    @Column(name = "severity_threshold", length = 20)
    @Enumerated(EnumType.STRING)
    private SeverityLevel severityThreshold = SeverityLevel.LOW;

    @Column(name = "campus_zone_filter", length = 100)
    private String campusZoneFilter;

    public NotificationPreference() {}

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }

    public SeverityLevel getSeverityThreshold() { return severityThreshold; }
    public void setSeverityThreshold(SeverityLevel severityThreshold) { this.severityThreshold = severityThreshold; }

    public String getCampusZoneFilter() { return campusZoneFilter; }
    public void setCampusZoneFilter(String campusZoneFilter) { this.campusZoneFilter = campusZoneFilter; }
}
