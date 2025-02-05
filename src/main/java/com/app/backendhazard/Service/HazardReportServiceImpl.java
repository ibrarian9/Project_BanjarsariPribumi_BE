package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Handler.ExcelDateConverter;
import com.app.backendhazard.Handler.FileUploadUtil;
import com.app.backendhazard.Models.*;
import com.app.backendhazard.Repository.*;
import com.app.backendhazard.Response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class HazardReportServiceImpl implements HazardReportService {

    private static final Logger log = LoggerFactory.getLogger(HazardReportServiceImpl.class);
    private final KategoriTemuanRepository kategoriTemuanRepo;
    private final DepartmentRepository departmentRepo;
    private final HazardReportRepository hazardReportRepo;
    private final HazardStatusHistoryRepository hazardStatusHistoryRepo;
    private final StatusRepository statusRepo;
    private final ResponseHelperService responseHelperService;

    @Override
    public ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar) {

        log.info("Hazard Report : {}", hazardReport );

        // Create & set new Report
        HazardReport newReport = new HazardReport();
        newReport.setTitle(hazardReport.getTitle());
        newReport.setNamaPelapor(hazardReport.getNamaPelapor());
        newReport.setLokasi(hazardReport.getLokasi());
        newReport.setDeskripsi(hazardReport.getDeskripsi());
        newReport.setTindakan(hazardReport.getTindakan());

        // Set Related id with error handling
        KategoriTemuan kategoriTemuan = kategoriTemuanRepo.findById(hazardReport.getKategoriTemuanId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori Temuan Not Found " + hazardReport.getKategoriTemuanId()));

        Department departmentPelapor = departmentRepo.findById(hazardReport.getDepartmentPelaporId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pelapor Not Found " + hazardReport.getDepartmentPelaporId()));

        Department departmentPerbaikan = departmentRepo.findById(hazardReport.getDepartmentPerbaikanId())
                .orElseThrow(() -> new EntityNotFoundException("Department Perbaikan Not Found " + hazardReport.getDepartmentPerbaikanId()));

        newReport.setKategoriTemuan(kategoriTemuan);
        newReport.setDepartmentPelapor(departmentPelapor);
        newReport.setDepartmentPerbaikan(departmentPerbaikan);
        newReport.setTanggalKejadian(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        // Save initial report without the image
        HazardReport savedReport = hazardReportRepo.save(newReport);

        // Handle Image Upload
        String nameGambar = "gambar_" + System.currentTimeMillis() + ".jpg";
        savedReport.setGambar(nameGambar);

        HazardReport hazardReportWithId = hazardReportRepo.save(savedReport);
        String uploadDir = "upload/hazardReport/" + hazardReportWithId.getId();

        // Save the image file
        try {
            FileUploadUtil.saveFile(uploadDir, nameGambar, gambar);
        } catch (Exception e){
            return responseHelperService.handleException(e);
        }

        // Add Status To History
        HazardStatusHistory history = new HazardStatusHistory();
        Status statusLaporan = statusRepo.findById(hazardReport.getStatusLaporanId())
                .orElseThrow(() -> new EntityNotFoundException("Status Laporan Not Found " + hazardReport.getStatusLaporanId()));
        history.setReport(hazardReportWithId);
        history.setStatus(statusLaporan);
        history.setUpdateBy("Admin");
        history.setUpdateDate(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        hazardStatusHistoryRepo.save(history);
        return responseHelperService.saveEntityWithMessage("Hazard Report Berhasil Dibuat!");
    }

    @Override
    public ResponseEntity<?> imageForHazardReport(Long id) {
        HazardReport hazardReport = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id)).getReport();

        String imageUrl = "upload/hazardReport/" + hazardReport.getId() + "/" + hazardReport.getGambar();

        return responseHelperService.fetchImageReport(imageUrl, "Hazard Report Image Not Found");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id, HttpServletRequest request) {
        // Check id
        HazardStatusHistory hazardReport = hazardStatusHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id));

        // Penyelesaian DTO
        ResponsePenyelesaianDTO penyelesaianDTO = null;
        Penyelesaian penyelesaian = hazardReport.getReport().getPenyelesaian();
        if (penyelesaian != null){
            penyelesaianDTO = new ResponsePenyelesaianDTO();
            penyelesaianDTO.setNamaPenyelesaian(hazardReport.getReport().getPenyelesaian().getNamaPenyelesaian());
            penyelesaianDTO.setDepartment(hazardReport.getReport().getPenyelesaian().getDepartment());
            penyelesaianDTO.setLinkGambar(responseHelperService.buildLinkImage(request, hazardReport.getReport().getPenyelesaian().getId(), "api/resolutionImage/"));
        }

        // Map Hazard Report fields to DTO
        ResponseHazardReportDTO resHazardReportDTO = new ResponseHazardReportDTO();
        resHazardReportDTO.setId(hazardReport.getReport().getId());
        resHazardReportDTO.setTitle(hazardReport.getReport().getTitle());
        resHazardReportDTO.setNamaPelapor(hazardReport.getReport().getNamaPelapor());
        resHazardReportDTO.setLokasi(hazardReport.getReport().getLokasi());
        resHazardReportDTO.setDeskripsi(hazardReport.getReport().getDeskripsi());
        resHazardReportDTO.setKategoriTemuan(hazardReport.getReport().getKategoriTemuan());
        resHazardReportDTO.setDepartmentPelapor(hazardReport.getReport().getDepartmentPelapor());
        resHazardReportDTO.setDepartmentPerbaikan(hazardReport.getReport().getDepartmentPerbaikan());
        resHazardReportDTO.setPenyelesaian(penyelesaianDTO);
        resHazardReportDTO.setTindakan(hazardReport.getReport().getTindakan());
        resHazardReportDTO.setTanggalKejadian(hazardReport.getReport().getTanggalKejadian());
        resHazardReportDTO.setLinkGambar(responseHelperService.buildLinkImage(request, hazardReport.getReport().getId(), "api/gambar/"));

        // HIstory Status
        HistoryStatusDTO historyStatusDTO = new HistoryStatusDTO();
        historyStatusDTO.setId(hazardReport.getId());
        historyStatusDTO.setReport(resHazardReportDTO);
        historyStatusDTO.setStatus(hazardReport.getStatus());
        historyStatusDTO.setAlasan(hazardReport.getAlasan());
        historyStatusDTO.setUpdateBy(hazardReport.getUpdateBy());
        historyStatusDTO.setUpdateDate(hazardReport.getUpdateDate());

        // Create the response map
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", historyStatusDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search) {
        List<HazardStatusHistory> data = hazardStatusHistoryRepo.searchHazardStatusHistory(search);
        data.sort(Comparator.comparing(HazardStatusHistory::getId).reversed());
        return responseHelperService.getAllData(data);
    }

    @Override
    public ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status) {
        return responseHelperService.getAllData(hazardStatusHistoryRepo.filterByDepartmentAndStatus(dept, status));
    }

    @Override
    public ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO) {
        // Cek id hazard report
        HazardReport hazardReport = hazardReportRepo.findById(hazardStatusDTO.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("Report Not Found " + hazardStatusDTO.getReportId()));
        // Cek id Status
        Status status = statusRepo.findById(hazardStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + hazardStatusDTO.getStatusId()));
        // Cek id History Hazard
        HazardStatusHistory history = hazardStatusHistoryRepo.findByReportId(hazardStatusDTO.getReportId())
                .orElse(new HazardStatusHistory());

        history.setReport(hazardReport);
        history.setStatus(status);

        // Cek when status reject, alasan required
        int reject = 3;
        if (status.getId() != null && status.getId().equals(reject)) {
            if (!StringUtils.hasText(hazardStatusDTO.getAlasan())) {
                ErrorResponse errorRes = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Alasan is required when status is Rejected");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
            }
            history.setAlasan(hazardStatusDTO.getAlasan());
        } else {
            history.setAlasan(hazardStatusDTO.getAlasan());
        }

        history.setUpdateBy(hazardStatusDTO.getUpdateBy());
        history.setUpdateDate(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        // Update Status Hazard Report
        hazardStatusHistoryRepo.save(history);

        return responseHelperService.saveEntityWithMessage("Status Hazard Report updated successfully!");
    }

    @Override
    public ResponseEntity<?> deleteHistoryStatus(Long id) {
        // fetch hazard report status id
        HazardStatusHistory hazardStatusHistory = hazardStatusHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id));

        HazardReport hazardReport = hazardStatusHistory.getReport();

        // Define tje file paths
        String fileName = hazardReport.getGambar();
        String filePath =  "upload/hazardReport/" + hazardReport.getId();
        File fileToDelete = new File(filePath, fileName);

        // Delete the specific file if it exists
        if (fileToDelete.exists() && !fileToDelete.delete()) {
            return responseHelperService.handleExceptionByMessage("Failed to delete the file: " + fileToDelete.getPath());
        }

        // Delete the entire directory if it exists
        File directory = new File(filePath);
        if (directory.exists() && directory.isDirectory()) {
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                return responseHelperService.handleException(e);
            }
        }
        // Delete hazard status history with this hazard report
        hazardStatusHistoryRepo.delete(hazardStatusHistory);

        // Delete the hazard report from the repository
        hazardReportRepo.delete(hazardReport);

        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("message", "Delete Berhasil");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> exportToExcel() {
        List<HazardStatusHistory> data = hazardStatusHistoryRepo.findAll();

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Hazard Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("No");
            headerRow.createCell(1).setCellValue("Tanggal");
            headerRow.createCell(2).setCellValue("Judul");
            headerRow.createCell(3).setCellValue("Nama");
            headerRow.createCell(4).setCellValue("Department");
            headerRow.createCell(5).setCellValue("Deskripsi");
            headerRow.createCell(6).setCellValue("Lokasi");
            headerRow.createCell(7).setCellValue("Jenis Temuan");
            headerRow.createCell(8).setCellValue("Tindakan Perbaikan");
            headerRow.createCell(9).setCellValue("Department Perbaikan");
            headerRow.createCell(10).setCellValue("Status");

            int rowNum = 1;
            for (HazardStatusHistory report : data) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                if (report.getReport().getTanggalKejadian() != null){
                    row.createCell(1).setCellValue(ExcelDateConverter.formatDate(report.getReport().getTanggalKejadian()));
                }
                row.createCell(2).setCellValue(report.getReport().getTitle());
                row.createCell(3).setCellValue(report.getReport().getNamaPelapor());
                row.createCell(4).setCellValue(report.getReport().getDepartmentPelapor().getNamaDepartment());
                row.createCell(5).setCellValue(report.getReport().getDeskripsi());
                row.createCell(6).setCellValue(report.getReport().getLokasi());
                row.createCell(7).setCellValue(report.getReport().getKategoriTemuan().getKategoriTemuan());
                row.createCell(8).setCellValue(report.getReport().getTindakan());
                row.createCell(9).setCellValue(report.getReport().getDepartmentPerbaikan().getNamaDepartment());
                row.createCell(10).setCellValue(report.getStatus().getNamaStatus());

                rowNum++;
            }

            // Generate Filename with Timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "hazard_report_" + timestamp + ".xlsx";

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=" + filename);

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e){
            return responseHelperService.handleException(e);
        }
    }
}
