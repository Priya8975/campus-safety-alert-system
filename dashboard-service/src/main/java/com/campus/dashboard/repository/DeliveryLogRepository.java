package com.campus.dashboard.repository;

import com.campus.dashboard.model.DeliveryLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeliveryLogRepository extends JpaRepository<DeliveryLog, UUID> {

    List<DeliveryLog> findByAlertId(UUID alertId);
}
