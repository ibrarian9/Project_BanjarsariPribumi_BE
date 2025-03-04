package com.app.backendhazard.Repository;

import com.app.backendhazard.Models.HazardStatusHistory;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface HazardStatusHistoryRepository extends JpaRepository<HazardStatusHistory, Long>, JpaSpecificationExecutor<HazardStatusHistory> {
    Optional<HazardStatusHistory> findByReportId(Long reportId);

    @Query("SELECT h FROM HazardStatusHistory h WHERE "
            + "(:search IS NULL OR LOWER(h.report.namaPelapor) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.report.title) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.report.lokasi) LIKE LOWER(CONCAT('%', :search, '%')) OR "
            + "LOWER(h.status.namaStatus) LIKE LOWER(CONCAT('%', :search, '%')))")
    Page<HazardStatusHistory> searchHazardStatusHistory(@Param("search") String search, Pageable pageable);

    @Query("SELECT h FROM HazardStatusHistory h WHERE "
            + "(:department IS NULL OR LOWER(h.report.departmentPelapor.namaDepartment) LIKE LOWER(CONCAT('%', :department, '%')) "
            + "OR LOWER(h.report.departmentPerbaikan.namaDepartment) LIKE LOWER(CONCAT('%', :department, '%'))) AND "
            + "(:status IS NULL OR LOWER(h.status.namaStatus) LIKE LOWER(CONCAT('%', :status, '%')))")
    List<HazardStatusHistory> filterByDepartmentAndStatus(
            @Param("department") String department,
            @Param("status") String status);

    @NotNull
    Page<HazardStatusHistory> findAll(Specification<HazardStatusHistory> spec, @NotNull Pageable pageable);

    @Query("SELECT COUNT(h) from HazardStatusHistory h WHERE h.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);

}
