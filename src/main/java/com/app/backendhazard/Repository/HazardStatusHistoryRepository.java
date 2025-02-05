package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.HazardStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HazardStatusHistoryRepository extends JpaRepository<HazardStatusHistory, Long> {
    Optional<HazardStatusHistory> findByReportId(Long reportId);

    @Query("SELECT h FROM HazardStatusHistory h WHERE "
            + "(:search IS NULL OR LOWER(h.report.namaPelapor) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.report.title) LIKE LOWER(CONCAT('%', :search, '$')) OR "
            + "LOWER(h.report.lokasi) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.status.namaStatus) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.report.departmentPelapor.namaDepartment) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.report.departmentPerbaikan.namaDepartment) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<HazardStatusHistory> searchHazardStatusHistory(@Param("search") String search);

    @Query("SELECT h FROM HazardStatusHistory h WHERE "
            + "(:department IS NULL OR LOWER(h.report.departmentPelapor.namaDepartment) LIKE LOWER(CONCAT('%', :department, '%')) "
            + "OR LOWER(h.report.departmentPerbaikan.namaDepartment) LIKE LOWER(CONCAT('%', :department, '%'))) AND "
            + "(:status IS NULL OR LOWER(h.status.namaStatus) LIKE LOWER(CONCAT('%', :status, '%')))")
    List<HazardStatusHistory> filterByDepartmentAndStatus(
            @Param("department") String department,
            @Param("status") String status);
}
