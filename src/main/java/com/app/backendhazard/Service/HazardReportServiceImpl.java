package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Handler.ExcelDateConverter;
import com.app.backendhazard.Handler.FileUploadUtil;
import com.app.backendhazard.Handler.FolderImageApp;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.app.backendhazard.Service.DailyInspectionServiceImpl.getResponseEntity;

@Service
@AllArgsConstructor
public class HazardReportServiceImpl implements HazardReportService {

    private final KategoriTemuanRepository kategoriTemuanRepo;
    private final DepartmentRepository departmentRepo;
    private final HazardReportRepository hazardReportRepo;
    private final HazardStatusHistoryRepository hazardStatusHistoryRepo;
    private final StatusRepository statusRepo;
    private final ResponseHelperService responseHelperService;
    private final FolderImageApp folderImageApp;
    private final UsersRepository usersRepository;

    @Transactional
    @Override
    public ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar) {

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

        Users users = usersRepository.findById(hazardReport.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Users Not Found " + hazardReport.getUserId()));

        newReport.setKategoriTemuan(kategoriTemuan);
        newReport.setDepartmentPelapor(departmentPelapor);
        newReport.setDepartmentPerbaikan(departmentPerbaikan);
        newReport.setTanggalKejadian(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        // Save initial report without the image
        HazardReport savedReport = hazardReportRepo.save(newReport);

        // Handle Image Upload
        String nameGambar = "gambar_" + UUID.randomUUID() + ".jpeg";
        savedReport.setGambar(nameGambar);

        HazardReport hazardReportWithId = hazardReportRepo.save(savedReport);
        String uploadDir = "hazardReport/" + hazardReportWithId.getId();

        // Save the image file
        try {
            FileUploadUtil.saveFile(folderImageApp.getFolderPath(), uploadDir, nameGambar, gambar);
        } catch (Exception e){
            return responseHelperService.handleException(e);
        }

        // Add Status To History
        HazardStatusHistory history = new HazardStatusHistory();
        Status statusLaporan = statusRepo.findById(hazardReport.getStatusLaporanId())
                .orElseThrow(() -> new EntityNotFoundException("Status Laporan Not Found " + hazardReport.getStatusLaporanId()));
        history.setUser(users);
        history.setReport(hazardReportWithId);
        history.setStatus(statusLaporan);
        history.setUpdateBy(users.getUsername());
        history.setUpdateDate(ZonedDateTime.now(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        hazardStatusHistoryRepo.save(history);
        return responseHelperService.saveEntityWithMessage("Hazard Report Berhasil Dibuat!");
    }

    @Override
    public ResponseEntity<?> imageForHazardReport(Long id) {
        HazardReport hazardReport = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id)).getReport();

        String imageUrl = folderImageApp.getFolderPath() + "ReportPic/hazardReport/" + hazardReport.getId() + "/" + hazardReport.getGambar();

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

    @Transactional
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

        if (hazardStatusDTO.getReportId() == null || hazardStatusDTO.getStatusId() == null) {
            return responseHelperService.handleExceptionByMessage("Report ID and Status ID are required!");
        }

        int reject = 3;
        // Cek when status reject, alasan required
        if (status.getId() != null && status.getId().equals(reject)) {
            if (!StringUtils.hasText(hazardStatusDTO.getAlasan())) {
                ErrorResponse errorRes = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Reason is required when status is Rejected");
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

    @Transactional
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

            String[] headers = {
                    "No", "Tanggal", "Judul Laporan", "Nama Pelapor", "Department Pelapor",
                    "Perusahaan Pelapor", "Deskripsi", "Lokasi", "Kategori Laporan",
                    "Nama Karyawan Perbaikan", "Tindakan Perbaikan",
                    "Department Perbaikan", "Perusahaan Perbaikan", "Status"
            };

            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (HazardStatusHistory report : data) {
                Row row = sheet.createRow(rowNum);
                Object[] values = {
                        rowNum,
                        report.getReport().getTanggalKejadian() != null ?
                                ExcelDateConverter.formatDate(report.getReport().getTanggalKejadian()) : "-",
                        report.getReport().getTitle(),
                        report.getReport().getNamaPelapor(),
                        report.getReport().getDepartmentPelapor().getNamaDepartment(),
                        report.getReport().getDepartmentPelapor().getCompany().getNamaCompany(),
                        report.getReport().getDeskripsi(),
                        report.getReport().getLokasi(),
                        report.getReport().getKategoriTemuan().getKategoriTemuan(),
                        report.getReport().getPenyelesaian() != null ?
                                report.getReport().getPenyelesaian().getNamaPenyelesaian() : "-",
                        report.getReport().getTindakan(),
                        report.getReport().getDepartmentPerbaikan().getNamaDepartment(),
                        report.getReport().getDepartmentPerbaikan().getCompany().getNamaCompany(),
                        report.getStatus().getNamaStatus()
                };

                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i].toString());
                }

                rowNum++;
            }

            // Generate Filename with Timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "hazard_report_" + timestamp + ".xlsx";

            return getResponseEntity(workbook, filename);
        } catch (IOException e){
            return responseHelperService.handleException(e);
        }
    }
}
