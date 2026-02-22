package com.campus.alertprocessing.service;

import com.campus.alertprocessing.model.NotificationPreference;
import com.campus.alertprocessing.model.SeverityLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, UUID> {

    @Query("SELECT DISTINCT np.channel FROM NotificationPreference np " +
           "WHERE np.severityThreshold <= :severity " +
           "AND (np.campusZoneFilter IS NULL OR np.campusZoneFilter = :zone)")
    List<String> findChannelsForAlert(@Param("severity") SeverityLevel severity,
                                      @Param("zone") String zone);

    @Query("SELECT COUNT(DISTINCT np.user) FROM NotificationPreference np " +
           "WHERE np.severityThreshold <= :severity " +
           "AND (np.campusZoneFilter IS NULL OR np.campusZoneFilter = :zone)")
    long countRecipientsForAlert(@Param("severity") SeverityLevel severity,
                                  @Param("zone") String zone);
}
