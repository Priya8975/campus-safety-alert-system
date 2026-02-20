package com.campus.alertingestion.controller;

import com.campus.alertingestion.dto.AlertResponse;
import com.campus.alertingestion.dto.CreateAlertRequest;
import com.campus.alertingestion.model.Alert;
import com.campus.alertingestion.service.AlertService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "*")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @PostMapping
    public ResponseEntity<AlertResponse> createAlert(@Valid @RequestBody CreateAlertRequest request) {
        Alert alert = alertService.createAlert(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(AlertResponse.from(alert));
    }

    @GetMapping
    public ResponseEntity<List<AlertResponse>> getActiveAlerts() {
        List<AlertResponse> alerts = alertService.getActiveAlerts()
                .stream()
                .map(AlertResponse::from)
                .toList();
        return ResponseEntity.ok(alerts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlertResponse> getAlert(@PathVariable UUID id) {
        Alert alert = alertService.getAlert(id);
        return ResponseEntity.ok(AlertResponse.from(alert));
    }
}
