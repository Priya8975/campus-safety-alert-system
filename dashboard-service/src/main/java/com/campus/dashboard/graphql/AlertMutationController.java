package com.campus.dashboard.graphql;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.service.AlertService;
import com.campus.dashboard.kafka.AlertEventProducer;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.stereotype.Controller;

import java.util.Map;
import java.util.UUID;

@Controller
public class AlertMutationController {

    private final AlertService alertService;
    private final AlertEventProducer alertEventProducer;

    public AlertMutationController(AlertService alertService, AlertEventProducer alertEventProducer) {
        this.alertService = alertService;
        this.alertEventProducer = alertEventProducer;
    }

    @MutationMapping
    public Alert createAlert(@Argument Map<String, Object> input) {
        String title = (String) input.get("title");
        String description = (String) input.get("description");
        String severityStr = (String) input.get("severity");
        double lat = ((Number) input.get("lat")).doubleValue();
        double lng = ((Number) input.get("lng")).doubleValue();
        String campusZone = (String) input.get("campusZone");

        com.campus.dashboard.model.Severity severity =
                com.campus.dashboard.model.Severity.valueOf(severityStr);

        Alert alert = alertService.createAlert(title, description, severity, lat, lng, campusZone);

        // Publish to Kafka for processing pipeline
        alertEventProducer.publish(alert);

        return alert;
    }

    @MutationMapping
    public Alert acknowledgeAlert(@Argument String id) {
        return alertService.acknowledgeAlert(UUID.fromString(id));
    }

    @MutationMapping
    public Alert resolveAlert(@Argument String id) {
        return alertService.resolveAlert(UUID.fromString(id));
    }
}
