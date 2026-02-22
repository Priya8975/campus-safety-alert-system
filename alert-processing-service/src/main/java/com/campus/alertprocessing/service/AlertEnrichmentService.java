package com.campus.alertprocessing.service;

import com.campus.alertprocessing.kafka.AlertEnrichedEvent;
import com.campus.alertprocessing.model.SeverityLevel;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class AlertEnrichmentService {

    private static final Logger log = LoggerFactory.getLogger(AlertEnrichmentService.class);

    private final GeoService geoService;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    public AlertEnrichmentService(GeoService geoService,
                                   NotificationPreferenceRepository preferenceRepository,
                                   UserRepository userRepository) {
        this.geoService = geoService;
        this.preferenceRepository = preferenceRepository;
        this.userRepository = userRepository;
    }

    public AlertEnrichedEvent enrich(JsonNode event) {
        UUID alertId = UUID.fromString(event.get("alertId").asText());
        String title = event.get("title").asText();
        String description = event.has("description") && !event.get("description").isNull()
                ? event.get("description").asText() : null;
        String severity = event.get("severity").asText();
        double lat = event.get("lat").asDouble();
        double lng = event.get("lng").asDouble();
        String campusZone = event.has("campusZone") && !event.get("campusZone").isNull()
                ? event.get("campusZone").asText() : null;

        // 1. Find affected buildings using geo-radius calculation
        List<String> affectedBuildings = geoService.findAffectedBuildings(lat, lng);
        log.info("Alert {} affects {} buildings within radius", alertId, affectedBuildings.size());

        // 2. Determine recipient count and channels from user preferences
        SeverityLevel severityLevel = SeverityLevel.valueOf(severity);
        List<String> channels = preferenceRepository.findChannelsForAlert(severityLevel, campusZone);
        long recipientCount = preferenceRepository.countRecipientsForAlert(severityLevel, campusZone);

        // If no preferences configured yet, default to all channels with estimated count
        if (channels.isEmpty()) {
            channels = List.of("push", "email", "sms");
            recipientCount = userRepository.count();
            if (recipientCount == 0) {
                recipientCount = 3400; // simulated recipient count
            }
        }

        log.info("Alert {} will be sent to {} recipients via channels: {}",
                alertId, recipientCount, channels);

        // 3. Build enriched event
        AlertEnrichedEvent enriched = new AlertEnrichedEvent();
        enriched.setAlertId(alertId);
        enriched.setTitle(title);
        enriched.setDescription(description);
        enriched.setSeverity(severity);
        enriched.setLat(lat);
        enriched.setLng(lng);
        enriched.setCampusZone(campusZone);
        enriched.setAffectedBuildings(affectedBuildings);
        enriched.setRecipientCount(recipientCount);
        enriched.setChannels(channels);
        enriched.setTimestamp(Instant.now());

        return enriched;
    }
}
