package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DailyInspection;
import com.app.backendhazard.Models.DetailDailyInspection;
import com.app.backendhazard.Models.InspectionAnswer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetailDailyInspectionRepository extends JpaRepository<DetailDailyInspection, Long> {
    List<DetailDailyInspection> findByDailyInspectionId(Long id);

    @Query("SELECT DISTINCT d.dailyInspection FROM DetailDailyInspection d " +
                "JOIN d.dailyInspection di " +
                "JOIN d.inspectionAnswer ia " +
                "JOIN d.inspectionQuestion iq " +
                "WHERE (:search IS NULL OR " +
                "LOWER(di.namaPengawas) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(iq.question) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
                "LOWER(ia.catatan) LIKE LOWER(CONCAT('%', :search, '%')))"

    )
    Page<DailyInspection> searchInspections(@Param("search") String search, Pageable pageable);

    @Query("SELECT DISTINCT d.dailyInspection FROM DetailDailyInspection d " +
            "JOIN d.dailyInspection di " +
            "JOIN d.inspectionAnswer ia " +
            "JOIN d.inspectionQuestion iq "
    )
    List<DailyInspection> listAllInspection();

    @Query("SELECT i from InspectionAnswer i WHERE i.id = :id")
    InspectionAnswer findInspectionAnswerById(Long id);
}
