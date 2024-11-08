package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.DetailDailyInspection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DetailDailyInspectionRepository extends JpaRepository<DetailDailyInspection, Long> {
    List<DetailDailyInspection> findByDailyInspectionId(Long id);

    @Query("SELECT d FROM DetailDailyInspection d " +
            "JOIN d.dailyInspection di " +
            "JOIN d.inspectionAnswer ia " +
            "JOIN d.inspectionQuestion iq " +
            "WHERE :search IS NULL OR " +
            "LOWER(di.namaPengawas) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(di.tanggalInspeksi) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(iq.question) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(ia.catatan) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<DetailDailyInspection> searchInspections(@Param("search") String search);
}
