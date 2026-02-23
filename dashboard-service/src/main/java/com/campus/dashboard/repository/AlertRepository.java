package com.campus.dashboard.repository;

import com.campus.dashboard.model.Alert;
import com.campus.dashboard.model.AlertStatus;
import com.campus.dashboard.model.Severity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertRepository extends JpaRepository<Alert, UUID> {

    List<Alert> findByStatusOrderByCreatedAtDesc(AlertStatus status);

    @Query("SELECT a FROM Alert a WHERE " +
           "(:zone IS NULL OR a.campusZone = :zone) AND " +
           "(:severity IS NULL OR a.severity = :severity) " +
           "ORDER BY a.createdAt DESC")
    List<Alert> findAlertHistory(@Param("zone") String zone,
                                 @Param("severity") Severity severity);
}
