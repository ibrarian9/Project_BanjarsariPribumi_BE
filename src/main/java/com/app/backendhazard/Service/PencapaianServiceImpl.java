package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.PenyelesaianDTO;
import com.app.backendhazard.Handler.FileUploadUtil;
import com.app.backendhazard.Handler.FolderImageApp;
import com.app.backendhazard.Models.*;
import com.app.backendhazard.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class PencapaianServiceImpl implements PencapaianService {

    private final PencapaianRepository pencapaianRepo;
    private final DepartmentRepository departmentRepo;
    private final PenyelesaianRepository penyelesaianRepo;
    private final HazardReportRepository hazardReportRepo;
    private final HazardStatusHistoryRepository hazardStatusHistoryRepo;
    private final ResponseHelperService responseHelperService;
    private final StatusRepository statusRepository;
    private final FolderImageApp folderImageApp;

    @Override
    public ResponseEntity<Map<String, Object>> addPencapaian(Pencapaian pencapaian) {
        return responseHelperService.saveEntity(pencapaian, pencapaianRepo);
    }

    @Transactional
    @Override
    public ResponseEntity<?> addPenyelesaian(Long id, PenyelesaianDTO penyelesaian, MultipartFile gambar) {
        // check id department & hazard report exist
        Department department = departmentRepo.findById(penyelesaian.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found"));
        HazardReport hazardReport = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found"))
                .getReport();

        HazardStatusHistory hazardStatusHistory = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found"));

        Status status = statusRepository.findById(2L)
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found"));

        // Create or retrieve existing Penyelesaian
        Penyelesaian penyelesaianEntity = hazardReport.getPenyelesaian();
        if (penyelesaianEntity == null) {
            penyelesaianEntity = new Penyelesaian();
        }

        // Set Penyelesaian
        penyelesaianEntity.setDepartment(department);
        penyelesaianEntity.setNamaPenyelesaian(penyelesaian.getNamaPenyelesaian());

        // Save Penyelesaian to assign an ID before handling the image
        penyelesaianEntity = penyelesaianRepo.save(penyelesaianEntity);

        // Check the ir have image or not
        if (gambar != null && !gambar.isEmpty()) {
            String oldImage = penyelesaianEntity.getGambar();

            // make name for image
            String namaGambar = "resolution_" + UUID.randomUUID() + ".jpeg";
            penyelesaianEntity.setGambar(namaGambar);

            String uploadDir = "resolution/" + penyelesaianEntity.getId();
            try {
                if (oldImage != null && !oldImage.isEmpty()) {
                    File oldFile = new File(uploadDir, oldImage);
                    if (oldFile.exists() && !oldFile.delete()) {
                        return responseHelperService.handleException(new IOException("Failed to delete old file: " + oldImage));
                    }
                }
                // Save the new image
                FileUploadUtil.saveFile(folderImageApp.getFolderPath(), uploadDir, namaGambar, gambar);

                // Save the update penyelesaian Entity with the new Image name
                penyelesaianEntity = penyelesaianRepo.save(penyelesaianEntity);
            } catch (Exception e) {
                return responseHelperService.handleException(e);
            }
        }

        // Update Hazard Report with the new Penyelesaian if not set
        if (hazardReport.getPenyelesaian() == null) {
            hazardReport.setPenyelesaian(penyelesaianEntity);
            hazardReportRepo.save(hazardReport);
        }

        hazardStatusHistory.setStatus(status);
        hazardStatusHistoryRepo.save(hazardStatusHistory);

        // Prepare Response
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", penyelesaianEntity.getId() == null ? "Resolution Added Succesfully" : "Resolution Updated Succesfully");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> imageForResolution(Long id) {
        Penyelesaian penyelesaian = penyelesaianRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Penyelesaian Not Found " + id));

        String imageUrl = folderImageApp.getFolderPath() + "ReportPic/resolution/" + penyelesaian.getId() + "/" + penyelesaian.getGambar();

        return responseHelperService.fetchImageReport(imageUrl, "Resolution Image Not Found");
    }
}
