package com.campus.alertingestion.repository;

import com.campus.alertingestion.model.Alert;
import com.campus.alertingestion.model.AlertStatus;
import com.campus.alertingestion.model.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findBySeverity(Severity severity);

    List<Alert> findByCampusZone(String campusZone);

    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);
}
