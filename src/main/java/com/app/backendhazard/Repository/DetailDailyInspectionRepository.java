package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DetailDailyInspection;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DetailDailyInspectionRepository extends JpaRepository<DetailDailyInspection, Long> {
    List<DetailDailyInspection> findByDailyInspectionId(Long id);
}
