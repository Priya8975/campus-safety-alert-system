package com.campus.dashboard;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.AlertStatus;
import com.campus.dashboard.model.Severity;
import com.campus.dashboard.repository.AlertRepository;
import com.campus.dashboard.service.AlertService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    private AlertService alertService;

    @BeforeEach
    void setUp() {
        alertService = new AlertService(alertRepository);
    }

    @Test
    void createAlert_shouldSaveAndReturn() {
        Alert saved = new Alert();
        saved.setId(UUID.randomUUID());
        saved.setTitle("Test Alert");
        saved.setSeverity(Severity.HIGH);
        saved.setStatus(AlertStatus.ACTIVE);

        when(alertRepository.save(any(Alert.class))).thenReturn(saved);

        Alert result = alertService.createAlert("Test Alert", "desc", Severity.HIGH,
                40.9136, -73.1235, "SAC");

        assertNotNull(result);
        assertEquals("Test Alert", result.getTitle());
        assertEquals(AlertStatus.ACTIVE, result.getStatus());

        ArgumentCaptor<Alert> captor = ArgumentCaptor.forClass(Alert.class);
        verify(alertRepository).save(captor.capture());
        assertEquals(Severity.HIGH, captor.getValue().getSeverity());
        assertEquals(40.9136, captor.getValue().getLat());
    }

    @Test
    void getActiveAlerts_shouldReturnActiveOnly() {
        Alert active = new Alert();
        active.setStatus(AlertStatus.ACTIVE);
        when(alertRepository.findByStatusOrderByCreatedAtDesc(AlertStatus.ACTIVE))
                .thenReturn(List.of(active));

        List<Alert> results = alertService.getActiveAlerts();

        assertEquals(1, results.size());
        assertEquals(AlertStatus.ACTIVE, results.get(0).getStatus());
    }

    @Test
    void acknowledgeAlert_shouldUpdateStatus() {
        UUID id = UUID.randomUUID();
        Alert alert = new Alert();
        alert.setId(id);
        alert.setStatus(AlertStatus.ACTIVE);

        when(alertRepository.findById(id)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Alert result = alertService.acknowledgeAlert(id);

        assertEquals(AlertStatus.ACKNOWLEDGED, result.getStatus());
    }

    @Test
    void resolveAlert_shouldUpdateStatus() {
        UUID id = UUID.randomUUID();
        Alert alert = new Alert();
        alert.setId(id);
        alert.setStatus(AlertStatus.ACTIVE);

        when(alertRepository.findById(id)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Alert result = alertService.resolveAlert(id);

        assertEquals(AlertStatus.RESOLVED, result.getStatus());
    }

    @Test
    void acknowledgeAlert_notFound_shouldThrow() {
        UUID id = UUID.randomUUID();
        when(alertRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> alertService.acknowledgeAlert(id));
    }

    @Test
    void getAlertHistory_withLimit_shouldTruncate() {
        Alert a1 = new Alert();
        Alert a2 = new Alert();
        Alert a3 = new Alert();
        when(alertRepository.findAlertHistory(null, null)).thenReturn(List.of(a1, a2, a3));

        List<Alert> results = alertService.getAlertHistory(null, null, 2);

        assertEquals(2, results.size());
    }
}
