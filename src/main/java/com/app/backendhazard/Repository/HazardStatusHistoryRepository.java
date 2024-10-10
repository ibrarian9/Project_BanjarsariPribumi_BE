package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.HazardStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface HazardStatusHistoryRepository extends JpaRepository<HazardStatusHistory, Long> {
    Optional<HazardStatusHistory> findByReportId(Long reportId);
}
