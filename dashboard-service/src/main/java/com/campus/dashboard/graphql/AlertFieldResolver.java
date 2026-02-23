package com.campus.dashboard.graphql;

import com.campus.dashboard.graphql.dto.DeliveryStatsDto;
import com.campus.dashboard.graphql.dto.LocationDto;
import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.DeliveryLog;
import com.campus.dashboard.service.DeliveryStatsService;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.Collections;
import java.util.List;

@Controller
public class AlertFieldResolver {

    private final DeliveryStatsService deliveryStatsService;

    public AlertFieldResolver(DeliveryStatsService deliveryStatsService) {
        this.deliveryStatsService = deliveryStatsService;
    }

    @SchemaMapping(typeName = "Alert", field = "location")
    public LocationDto location(Alert alert) {
        return new LocationDto(
                alert.getLat(),
                alert.getLng(),
                alert.getCampusZone(),
                Collections.emptyList()
        );
    }

    @SchemaMapping(typeName = "Alert", field = "deliveryStats")
    public DeliveryStatsDto deliveryStats(Alert alert) {
        List<DeliveryLog> logs = deliveryStatsService.getDeliveryLogs(alert.getId());
        if (logs.isEmpty()) {
            return null;
        }
        return DeliveryStatsDto.fromLogs(alert.getId().toString(), logs);
    }
}
