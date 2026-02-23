package com.campus.dashboard.graphql;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.graphql.dto.DeliveryStatsDto;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Sinks;

import java.util.concurrent.atomic.AtomicInteger;

@Controller
public class AlertSubscriptionController {

    private static final Logger log = LoggerFactory.getLogger(AlertSubscriptionController.class);

    private final Sinks.Many<Alert> alertSink;
    private final Sinks.Many<DeliveryStatsDto> deliverySink;
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    public AlertSubscriptionController(MeterRegistry meterRegistry) {
        this.alertSink = Sinks.many().multicast().onBackpressureBuffer();
        this.deliverySink = Sinks.many().multicast().onBackpressureBuffer();

        Gauge.builder("websocket.connections.active", activeConnections, AtomicInteger::get)
                .description("Number of active WebSocket subscription connections")
                .register(meterRegistry);
    }

    @SubscriptionMapping
    public Publisher<Alert> alertCreated() {
        activeConnections.incrementAndGet();
        log.info("New subscription: alertCreated [active={}]", activeConnections.get());
        return alertSink.asFlux()
                .doOnCancel(() -> {
                    activeConnections.decrementAndGet();
                    log.info("Subscription cancelled: alertCreated [active={}]", activeConnections.get());
                });
    }

    @SubscriptionMapping
    public Publisher<DeliveryStatsDto> deliveryUpdate(@Argument String alertId) {
        activeConnections.incrementAndGet();
        log.info("New subscription: deliveryUpdate [alertId={}, active={}]", alertId, activeConnections.get());
        return deliverySink.asFlux()
                .filter(stats -> alertId.equals(stats.getAlertId()))
                .doOnCancel(() -> {
                    activeConnections.decrementAndGet();
                    log.info("Subscription cancelled: deliveryUpdate [active={}]", activeConnections.get());
                });
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
