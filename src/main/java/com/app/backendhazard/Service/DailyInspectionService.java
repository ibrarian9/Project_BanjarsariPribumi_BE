package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.DailyInspectionStatusDTO;
import com.app.backendhazard.DTO.DetailInspectionDTO;
import com.app.backendhazard.DTO.InspectionRequestDTO;
import com.app.backendhazard.DTO.UpdateInspectionStatusDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@Service
public interface DailyInspectionService {
    ResponseEntity<Map<String, Object>> getInspectionQuestion(Long areakerjaId);
    ResponseEntity<?> addInspectionAnswer(InspectionRequestDTO requestDTO, List<MultipartFile> gambarFiles);
    ResponseEntity<Map<String, Object>> getDetailInspectionAnswer(Long id, HttpServletRequest request);
    ResponseEntity<Map<String, Object>> getAllDailyInspection(String search, Integer size, Integer page);
    ResponseEntity<?> editStatusDailyInspection(Long id, DailyInspectionStatusDTO dailyInspectionStatusDTO);
    ResponseEntity<?> editStatusAnswer(Long id, UpdateInspectionStatusDTO inspectionStatusDTO);
    ResponseEntity<?> deleteDailyInspection(Long id);
    ResponseEntity<Map<String, Object>> addDetailDailyInspection(DetailInspectionDTO detailInspectionDTO);
    ResponseEntity<?> imageForInspection(Long idDailyInspection, Long id);
    ResponseEntity<?> exportToExcel();
}
