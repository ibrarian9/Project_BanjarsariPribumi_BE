package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.HazardReport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HazardReportRepository extends JpaRepository<HazardReport, Long> {
}
