package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.HazardReportDTO;
import com.app.backendhazard.DTO.HazardStatusDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface HazardReportService {
    ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar);
    ResponseEntity<?> imageForHazardReport(Long id);
    ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id, HttpServletRequest request);
    ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search);
    ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status);
    ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO);
    ResponseEntity<?> deleteHistoryStatus(Long id);
    ResponseEntity<?> exportToExcel();
}
