package com.campus.dashboard.graphql;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.graphql.dto.DeliveryStatsDto;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

@Controller
public class AlertSubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(AlertSubscriptionController.class);

    private final Sinks.Many<Alert> alertSink;
    private final Sinks.Many<DeliveryStatsDto> deliverySink;

    public AlertSubscriptionController() {
        this.alertSink = Sinks.many().multicast().onBackpressureBuffer();
        this.deliverySink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @SubscriptionMapping
    public Publisher<Alert> alertCreated() {
        log.info("New subscription: alertCreated");
        return alertSink.asFlux();
    }

    @SubscriptionMapping
    public Publisher<DeliveryStatsDto> deliveryUpdate(@Argument String alertId) {
        log.info("New subscription: deliveryUpdate [alertId={}]", alertId);
        return deliverySink.asFlux()
                .filter(stats -> alertId.equals(stats.getAlertId()));
    }

    public void publishAlert(Alert alert) {
        alertSink.tryEmitNext(alert);
        log.info("Published alert to subscription [id={}]", alert.getId());
    }

    public void publishDeliveryUpdate(DeliveryStatsDto stats) {
        deliverySink.tryEmitNext(stats);
        log.info("Published delivery update to subscription [alertId={}]", stats.getAlertId());
    }
}
