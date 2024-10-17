package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DailyInspection;
import com.app.backendhazard.Models.Inspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DailyInspectionRepository extends JpaRepository<DailyInspection, Long> {
}
