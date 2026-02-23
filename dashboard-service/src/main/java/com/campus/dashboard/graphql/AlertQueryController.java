package com.campus.dashboard.graphql;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.Severity;
import com.campus.dashboard.service.AlertService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class AlertQueryController {

    private final AlertService alertService;

    public AlertQueryController(AlertService alertService) {
        this.alertService = alertService;
    }

    @QueryMapping
    public List<Alert> activeAlerts() {
        return alertService.getActiveAlerts();
    }

    @QueryMapping
    public Alert alert(@Argument String id) {
        return alertService.getAlert(UUID.fromString(id)).orElse(null);
    }

    @QueryMapping
    public List<Alert> alertHistory(@Argument String zone,
                                     @Argument Severity severity,
                                     @Argument Integer limit) {
        return alertService.getAlertHistory(zone, severity, limit);
    }
}
