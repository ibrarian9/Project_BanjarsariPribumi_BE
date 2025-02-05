package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DailyInspection;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DailyInspectionRepository extends JpaRepository<DailyInspection, Long> {
}
