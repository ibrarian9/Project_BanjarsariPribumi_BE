package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Models.Pencapaian;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface SystemService {
    ResponseEntity<Map<String, Object>> getAllCompany();
    ResponseEntity<Map<String, Object>> getDetailCompany(Long id);
    ResponseEntity<Map<String, Object>> addCompany(CompanyDTO company);
    ResponseEntity<Map<String, Object>> getAllDepartment();
    ResponseEntity<Map<String, Object>> getDetailDepartment(Long id);
    ResponseEntity<Map<String, Object>> addDepartment(DepartmentsDTO department);
    ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar);
    ResponseEntity<Map<String, Object>> getAllInspection();
    ResponseEntity<Map<String, Object>> getInspectionQuestion(Long areakerjaId);
    ResponseEntity<Map<String, Object>> getDetailInspection(Long id);
    ResponseEntity<?> addInspectionAnswer(InspectionRequestDTO requestDTO);
    ResponseEntity<Map<String, Object>> getDetailInspectionAnswer(Long id);
    ResponseEntity<Map<String, Object>> getAllDailyInspection(String search);
    ResponseEntity<?> editStatusDailyInspection(Long id, DailyInspectionStatusDTO dailyInspectionStatusDTO);
    ResponseEntity<Map<String, Object>> addDetailDailyInspection(DetailInspectionDTO detailInspectionDTO);
    ResponseEntity<Map<String, Object>> addPencapaian(Pencapaian pencapaian);
    ResponseEntity<?> addPenyelesaian(Long id, PenyelesaianDTO penyelesaian, MultipartFile gambar);
    ResponseEntity<?> imageForHazardReport(Long id);
    ResponseEntity<?> imageForResolution(Long id);
    ResponseEntity<Map<String, Object>> getAllSafetyTalk();
    ResponseEntity<Map<String, Object>> getDetailSafetyTalk(Long id);
    ResponseEntity<Map<String, Object>> addSafetyTalk(SafetyTalkDTO safetyTalk);
    ResponseEntity<Map<String, Object>> getAllStatusKaryawan();
    ResponseEntity<Map<String, Object>> getDetailStatusKaryawan(Long id);
    ResponseEntity<Map<String, Object>> getAllStatus();
    ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id, HttpServletRequest request);
    ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search);
    ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status);
    ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO);
    ResponseEntity<?> deleteHistoryStatus(Long id);
    ResponseEntity<Map<String, Object>> getAllUser();
    ResponseEntity<?> exportToExcel();
    ResponseEntity<Map<String, Object>> getAllWorkArea();
    ResponseEntity<Map<String, Object>> getAllShift();
    ResponseEntity<Map<String, Object>> getAllFindings();
    ResponseEntity<Map<String, Object>> getAllStatusCompany();
    ResponseEntity<Map<String, Object>> getDetailStatusCompany(Long id);
}
