package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DailyInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DailyInspectionRepository extends JpaRepository<DailyInspection, Long> {
    @Query("SELECT count(d) from DailyInspection d where d.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
}
