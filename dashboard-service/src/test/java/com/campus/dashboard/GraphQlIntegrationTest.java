package com.campus.dashboard;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.AlertStatus;
import com.campus.dashboard.model.Severity;
import com.campus.dashboard.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.graphql.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureHttpGraphQlTester
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"alert-created", "alert-delivery-status"})
@Tag("integration")
class GraphQlIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("campus_alerts")
            .withUsername("alerts_user")
            .withPassword("alerts_pass");

    @Autowired
    private HttpGraphQlTester graphQlTester;

    @Autowired
    private AlertRepository alertRepository;

    @BeforeEach
    void setUp() {
        alertRepository.deleteAll();
    }

    @Test
    void createAlert_mutation_shouldPersistAndReturn() {
        graphQlTester.document("""
                        mutation {
                            createAlert(input: {
                                title: "Power outage in Library"
                                severity: HIGH
                                lat: 40.9140
                                lng: -73.1230
                                campusZone: "Library"
                            }) {
                                id
                                title
                                severity
                                status
                            }
                        }
                        """)
                .execute()
                .path("createAlert.title").entity(String.class).isEqualTo("Power outage in Library")
                .path("createAlert.severity").entity(String.class).isEqualTo("HIGH")
                .path("createAlert.status").entity(String.class).isEqualTo("ACTIVE");

        assertEquals(1, alertRepository.count());
    }

    @Test
    void activeAlerts_query_shouldReturnActiveOnly() {
        // Seed data
        Alert active = createTestAlert("Active alert", Severity.HIGH, AlertStatus.ACTIVE);
        Alert resolved = createTestAlert("Resolved alert", Severity.LOW, AlertStatus.RESOLVED);
        alertRepository.save(active);
        alertRepository.save(resolved);

        graphQlTester.document("""
                        query {
                            activeAlerts {
                                id
                                title
                                status
                            }
                        }
                        """)
                .execute()
                .path("activeAlerts").entityList(Object.class).hasSize(1)
                .path("activeAlerts[0].title").entity(String.class).isEqualTo("Active alert");
    }

    @Test
    void acknowledgeAlert_mutation_shouldUpdateStatus() {
        Alert alert = createTestAlert("Test alert", Severity.MEDIUM, AlertStatus.ACTIVE);
        alert = alertRepository.save(alert);

        graphQlTester.document("""
                        mutation AckAlert($id: ID!) {
                            acknowledgeAlert(id: $id) {
                                id
                                status
                            }
                        }
                        """)
                .variable("id", alert.getId().toString())
                .execute()
                .path("acknowledgeAlert.status").entity(String.class).isEqualTo("ACKNOWLEDGED");

        Alert updated = alertRepository.findById(alert.getId()).orElseThrow();
        assertEquals(AlertStatus.ACKNOWLEDGED, updated.getStatus());
    }

    @Test
    void resolveAlert_mutation_shouldUpdateStatus() {
        Alert alert = createTestAlert("Resolve test", Severity.CRITICAL, AlertStatus.ACTIVE);
        alert = alertRepository.save(alert);

        graphQlTester.document("""
                        mutation ResolveAlert($id: ID!) {
                            resolveAlert(id: $id) {
                                id
                                status
                            }
                        }
                        """)
                .variable("id", alert.getId().toString())
                .execute()
                .path("resolveAlert.status").entity(String.class).isEqualTo("RESOLVED");
    }

    private Alert createTestAlert(String title, Severity severity, AlertStatus status) {
        Alert alert = new Alert();
        alert.setTitle(title);
        alert.setSeverity(severity);
        alert.setStatus(status);
        alert.setLat(40.9136);
        alert.setLng(-73.1235);
        alert.setCampusZone("SAC");
        alert.setCreatedAt(Instant.now());
        alert.setUpdatedAt(Instant.now());
        return alert;
    }
}
