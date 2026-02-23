package com.campus.alertingestion;

import com.campus.alertingestion.model.Alert;
import com.campus.alertingestion.model.AlertStatus;
import com.campus.alertingestion.model.Severity;
import com.campus.alertingestion.repository.AlertRepository;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@EmbeddedKafka(partitions = 1, topics = {"alert-created"})
@Tag("integration")
class AlertIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("campus_alerts")
            .withUsername("alerts_user")
            .withPassword("alerts_pass");

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AlertRepository alertRepository;

    @Test
    void createAlert_shouldPersistAndReturn201() throws Exception {
        String json = """
                {
                    "title": "Fire alarm in Engineering building",
                    "description": "Smoke detected on 3rd floor",
                    "severity": "CRITICAL",
                    "lat": 40.9136,
                    "lng": -73.1235,
                    "campusZone": "Engineering"
                }
                """;

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.title").value("Fire alarm in Engineering building"))
                .andExpect(jsonPath("$.severity").value("CRITICAL"))
                .andExpect(jsonPath("$.status").value("ACTIVE"));

        List<Alert> alerts = alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.ACTIVE);
        assertFalse(alerts.isEmpty());
        assertEquals("Fire alarm in Engineering building", alerts.get(0).getTitle());
        assertEquals(Severity.CRITICAL, alerts.get(0).getSeverity());
    }

    @Test
    void createAlert_missingTitle_shouldReturn400() throws Exception {
        String json = """
                {
                    "severity": "HIGH",
                    "lat": 40.9136,
                    "lng": -73.1235
                }
                """;

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getActiveAlerts_shouldReturnList() throws Exception {
        // Create a test alert first
        String json = """
                {
                    "title": "Test alert for listing",
                    "severity": "LOW",
                    "lat": 40.914,
                    "lng": -73.124,
                    "campusZone": "Library"
                }
                """;

        mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].title").isNotEmpty());
    }

    @Test
    void getAlertById_shouldReturnAlert() throws Exception {
        String json = """
                {
                    "title": "Flood warning near SAC",
                    "severity": "MEDIUM",
                    "lat": 40.912,
                    "lng": -73.121,
                    "campusZone": "SAC"
                }
                """;

        String response = mockMvc.perform(post("/api/alerts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Extract ID from response
        String id = com.fasterxml.jackson.databind.ObjectMapper.class.getDeclaredConstructor()
                .newInstance().readTree(response).get("id").asText();

        mockMvc.perform(get("/api/alerts/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Flood warning near SAC"))
                .andExpect(jsonPath("$.severity").value("MEDIUM"));
    }

    @Test
    void healthEndpoint_shouldReturnUp() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }

    @Test
    void prometheusEndpoint_shouldReturnMetrics() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("jvm_memory")));
    }
}
