package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Models.*;
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
    ResponseEntity<Map<String, Object>> getAllHazardReport();
    ResponseEntity<Map<String, Object>> getDetailedHazardReport(Long id);
    ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar);
    ResponseEntity<?> deleteHazardReport(Long id);
    ResponseEntity<Map<String, Object>> getAllInspection();
    ResponseEntity<Map<String, Object>> addInspection(DailyInspectionDTO inspection);
    ResponseEntity<Map<String, Object>> getInspectionQuestion(Long areakerjaId);
    ResponseEntity<Map<String, Object>> getDetailInspection(Long id);
    ResponseEntity<?> addInspectionAnswer(AnswerDTO answerDTO);
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
    ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id);
    ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search);
    ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status);
    ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO);
    ResponseEntity<?> deleteHistoryStatus(Long id);
    ResponseEntity<Map<String, Object>> getAllUser();
    ResponseEntity<?> exportToExcel();
}
