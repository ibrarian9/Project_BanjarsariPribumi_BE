package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Handler.FileUploadUtil;
import com.app.backendhazard.Models.*;
import com.app.backendhazard.Repository.*;
import com.app.backendhazard.Response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@AllArgsConstructor
public class SystemServiceImpl implements SystemService {

    private static final Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);
    private final UsersRepository usersRepo;
    private final ShiftRepository shiftRepo;
    private final StatusRepository statusRepo;
    private final StatusKaryawanRepository statusKaryawanRepo;
    private final HazardStatusHistoryRepository hazardStatusHistoryRepo;
    private final SafetyTalkRepo safetyTalkRepo;
    private final CompanyRepository companyRepo;
    private final CompanyRepository perusahaanRepo;
    private final AreaKerjaRepository areaKerjaRepo;
    private final DepartmentRepository departmentRepo;
    private final PencapaianRepository pencapaianRepo;
    private final PenyelesaianRepository penyelesaianRepo;
    private final HazardReportRepository hazardReportRepo;
    private final DailyInspectionRepository dailyInspectionRepo;
    private final StatusCompanyRepository statusCompanyRepository;
    private final String path = "src/main/resources/";

    private <T> ResponseEntity<Map<String, Object>> getAllData(List<T> list) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<Map<String, Object>> getDetailData(Long id, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        Optional<T> optionalData = repository.findById(id);
        optionalData.ifPresent(value -> response.put("data", value));
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<Map<String, Object>> saveEntity(T entity, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("data", repository.save(entity));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> saveEntityWithMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllCompany() {
        return getAllData(perusahaanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailCompany(Long id) {
        return getDetailData(id, perusahaanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addCompany(CompanyDTO company) {
        StatusCompany statusCompany = statusCompanyRepository.findById(company.getStatusCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found"));

        Company newCompany = new Company();
        newCompany.setNamaCompany(company.getNamaCompany());
        newCompany.setStatusCompany(statusCompany);

        return saveEntity(newCompany, perusahaanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllDepartment() {
        return getAllData(departmentRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailDepartment(Long id) {
        return getDetailData(id, departmentRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addDepartment(DepartmentsDTO department) {
        Company company = companyRepo.findById(department.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        Department newDepartment = new Department();
        newDepartment.setNamaDepartment(department.getNamaDepartment());
        newDepartment.setCompany(company);

        return saveEntity(newDepartment, departmentRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllHazardReport() {
        return getAllData(hazardReportRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailedHazardReport(Long id) {
        return getDetailData(id, hazardReportRepo);
    }

    @Override
    public ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar) {

        // Create & set new Report
        HazardReport newReport = new HazardReport();
        newReport.setTitle(hazardReport.getTitle());
        newReport.setNamaPelapor(hazardReport.getNamaPelapor());
        newReport.setLokasi(hazardReport.getLokasi());
        newReport.setDeskripsi(hazardReport.getDeskripsi());
        newReport.setTindakan(hazardReport.getTindakan());

        Department departmentPelapor = departmentRepo.findById(hazardReport.getDepartmentPelaporId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pelapor Not Found " + hazardReport.getDepartmentPelaporId()));

        Department departmentPerbaikan = departmentRepo.findById(hazardReport.getDepartmentPerbaikanId())
                .orElseThrow(() -> new EntityNotFoundException("Department Perbaikan Not Found " + hazardReport.getDepartmentPerbaikanId()));

        newReport.setDepartmentPelapor(departmentPelapor);
        newReport.setDepartmentPerbaikan(departmentPerbaikan);
        newReport.setTanggalKejadian(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        HazardReport savedReport = hazardReportRepo.save(newReport);

        // Handle Image Upload
        String nameGambar = "gambar_" + System.currentTimeMillis() + ".jpg";
        savedReport.setGambar(nameGambar);

        HazardReport hazardReport1 = hazardReportRepo.save(savedReport);
        String uploadDir = path + "upload/" + hazardReport1.getId();

        try {
            FileUploadUtil.saveFile(uploadDir, nameGambar, gambar);
        } catch (Exception e){
            return handleException(e);
        }

        // Add Status To History
        HazardStatusHistory history = new HazardStatusHistory();
        Status status = statusRepo.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Status Pelapor Not Found" + 1L));
        history.setReport(hazardReport1);
        history.setStatus(status);
        history.setUpdateBy("Admin");
        history.setUpdateDate(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        hazardStatusHistoryRepo.save(history);

        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", "Hazard Report Berhasil Dibuat!");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteHazardReport(Long id) {
        Optional<HazardReport> reportOptional = hazardReportRepo.findById(id);

        if (reportOptional.isPresent()) {
            HazardReport hazardReport = reportOptional.get();

            String fileName = hazardReport.getGambar();
            String filePath = path + "uploads/" + hazardReport.getId();
            File fileToDelete = new File(filePath, fileName);

            if (fileToDelete.exists() && !fileToDelete.delete()) {
                System.out.println("Failed to delete the file: " + fileToDelete.getPath());
            }

            File directory = new File(filePath);
            if (directory.exists() && directory.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e) {
                    System.out.println("Error deleting directory: " + e.getMessage());
                }
            }

            hazardReportRepo.delete(hazardReport);

            Map<String, Object> response = new HashMap<>();
            response.put("httpStatus", HttpStatus.OK.value());
            response.put("message", "Delete Berhasil");

            return ResponseEntity.ok(response);
        } else {
            return handleExceptionByMessage("Hazard Report Not Found");
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllInspection() {
        return getAllData(dailyInspectionRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> addInspection(DailyInspectionDTO inspection) {
        Department departmentPengawas = departmentRepo.findById(inspection.getDepartmentPengawasId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pengawas Not Found " + inspection.getDepartmentPengawasId()));

        Shift shiftKerja = shiftRepo.findById(inspection.getShiftKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Shift Kerja Not Found " + inspection.getShiftKerjaId()));

        AreaKerja areaKerja = areaKerjaRepo.findById(inspection.getAreaKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Area Kerja Not Found " + inspection.getAreaKerjaId()));

        DailyInspection dailyInspection = new DailyInspection();
        dailyInspection.setNamaPengawas(inspection.getNamaPengawas());
        dailyInspection.setDepartmentPengawas(departmentPengawas);
        dailyInspection.setShiftKerja(shiftKerja);
        dailyInspection.setAreaKerja(areaKerja);

        return saveEntity(dailyInspection, dailyInspectionRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailInspection(Long id) {
        return getDetailData(id, dailyInspectionRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addPencapaian(Pencapaian pencapaian) {
        return saveEntity(pencapaian, pencapaianRepo);
    }

    @Override
    public ResponseEntity<?> addPenyelesaian(Long id, PenyelesaianDTO penyelesaian, MultipartFile gambar) {
        Department department = departmentRepo.findById(penyelesaian.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found"));

        HazardReport hazardReport = hazardReportRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found"));

        Penyelesaian penyelesaianEntity = hazardReport.getPenyelesaian();

        if (penyelesaianEntity == null) {
            penyelesaianEntity = new Penyelesaian();
        }

        penyelesaianEntity.setDepartment(department);
        penyelesaianEntity.setNamaPenyelesaian(penyelesaian.getNamaPenyelesaian());

        if (gambar != null && !gambar.isEmpty()) {
            String oldImage = penyelesaianEntity.getGambar();

            String namaGambar = "resolution_" + System.currentTimeMillis() + ".jpg";
            penyelesaianEntity.setGambar(namaGambar);

            String uploadDir = path + "upload/resolution/" + penyelesaianEntity.getId();
            try {
                if (oldImage != null && !oldImage.isEmpty()) {
                    File oldFile = new File(uploadDir + "/" + oldImage);
                    if (oldFile.exists()) {
                        if (!oldFile.delete()) {
                            throw new IOException("Failed to delete old file: " + oldImage);
                        }
                    }
                }

                FileUploadUtil.saveFile(uploadDir, namaGambar, gambar);
            } catch (Exception e) {
                return handleException(e);
            }
        }

        Penyelesaian savePenyelesaian = penyelesaianRepo.save(penyelesaianEntity);

        if (hazardReport.getPenyelesaian() == null) {
            hazardReport.setPenyelesaian(savePenyelesaian);
            hazardReportRepo.save(hazardReport);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", penyelesaianEntity.getId() == null ? "Resolution Added Succesfully" : "Resolution Updated Succesfully");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> imageForHazardReport(Long id) {
        return fetchImage(() -> hazardReportRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id)),
                HazardReport::imagePath, "Hazard Report Image Not Found");
    }

    @Override
    public ResponseEntity<?> imageForResolution(Long id) {
        return fetchImage(() -> penyelesaianRepo.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Resolution Not Found " + id)),
                Penyelesaian::imagePath, "Resolution Image Not Found");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllSafetyTalk() {
        return getAllData(safetyTalkRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailSafetyTalk(Long id) {
        return getDetailData(id, safetyTalkRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addSafetyTalk(SafetyTalkDTO safetyTalk) {

        log.info("safety talk: {}", safetyTalk);

        Users users = usersRepo.findById(safetyTalk.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("User Not Found " + safetyTalk.getUserId()));

        Department department = departmentRepo.findById(safetyTalk.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found " + safetyTalk.getDepartmentId()));

        SafetyTalk newSafetyTalk = new SafetyTalk();
        newSafetyTalk.setDepartment(department);
        newSafetyTalk.setUser(users);
        newSafetyTalk.setAttaintmentNumber(safetyTalk.getAttainmentNum());
        newSafetyTalk.setTanggal(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        safetyTalkRepo.save(newSafetyTalk);

        return saveEntityWithMessage("Safety Talk Berhasil Ditambahkan");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatusKaryawan() {
        return getAllData(statusKaryawanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailStatusKaryawan(Long id) {
        return getDetailData(id, statusKaryawanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatus() {
        return getAllData(statusRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id) {
        return getDetailData(id, hazardStatusHistoryRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search) {
        return getAllData(hazardStatusHistoryRepo.searchHazardStatusHistory(search));
    }

    @Override
    public ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status) {
        return getAllData(hazardStatusHistoryRepo.filterByDepartmentAndStatus(dept, status));
    }

    @Override
    public ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO) {

        HazardReport hazardReport = hazardReportRepo.findById(hazardStatusDTO.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("Report Not Found " + hazardStatusDTO.getReportId()));

        Status status = statusRepo.findById(hazardStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + hazardStatusDTO.getStatusId()));

        HazardStatusHistory history = hazardStatusHistoryRepo.findByReportId(hazardStatusDTO.getReportId())
                .orElse(new HazardStatusHistory());

        history.setReport(hazardReport);
        history.setStatus(status);

        Integer reject = 3;
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

        hazardStatusHistoryRepo.save(history);

        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("message", "Status Hazard Report updated successfully!");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> deleteHistoryStatus(Long id) {
        Optional<HazardStatusHistory> reportOptional = hazardStatusHistoryRepo.findById(id);
        if (reportOptional.isPresent()) {
            HazardStatusHistory hazardStatusHistory = reportOptional.get();

            String fileName = hazardStatusHistory.getReport().getGambar();
            String filePath = path + "uploads/" + hazardStatusHistory.getId();
            File fileToDelete = new File(filePath, fileName);

            if (fileToDelete.exists() && !fileToDelete.delete()) {
                handleExceptionByMessage("Failed to delete file " + fileName);
            }

            File directory = new File(filePath);
            if (directory.exists() && directory.isDirectory()) {
                try {
                    FileUtils.deleteDirectory(directory);
                } catch (IOException e){
                    handleExceptionByMessage("Error while deleting file " + e.getMessage());
                }
            }

            hazardStatusHistoryRepo.delete(hazardStatusHistory);

            Map<String, Object> response = new HashMap<>();
            response.put("httpStatus", HttpStatus.OK.value());
            response.put("message", "Delete Report Berhasil");

            return ResponseEntity.ok(response);
        } else {
            return handleExceptionByMessage("Report Not Found " + id);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllUser() {
        return getAllData(usersRepo.findByRoleId(2L));
    }

    @Override
    public ResponseEntity<?> exportToExcel() {
        List<HazardStatusHistory> data = hazardStatusHistoryRepo.findAll();

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Hazard Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Title");
            headerRow.createCell(2).setCellValue("Nama Pelapor");
            headerRow.createCell(3).setCellValue("Lokasi");
            headerRow.createCell(4).setCellValue("Status");
            headerRow.createCell(5).setCellValue("Tindakan");
            headerRow.createCell(6).setCellValue("Update By");
            headerRow.createCell(7).setCellValue("Update Date");

            int rowNum = 1;
            for (HazardStatusHistory report : data) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(report.getId());
                row.createCell(1).setCellValue(report.getReport().getTitle());
                row.createCell(2).setCellValue(report.getReport().getNamaPelapor());
                row.createCell(3).setCellValue(report.getReport().getLokasi());
                row.createCell(4).setCellValue(report.getStatus().getNamaStatus());
                row.createCell(5).setCellValue(report.getReport().getTindakan());
                row.createCell(6).setCellValue(report.getUpdateBy());
                row.createCell(7).setCellValue(report.getUpdateDate().toString());
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=hazard_reports.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e){
            return handleException(e);
        }
    }

    private <T> ResponseEntity<?> fetchImage(Supplier<T> entitySupplier, Function<T, String> imagePathFunction, String notFoundMessage) {
        try {
            T entity = entitySupplier.get();
            String imagePath = imagePathFunction.apply(entity);

            log.info(imagePath);

            if (imagePath == null) {
                return handleExceptionByMessage(notFoundMessage);
            }

            ClassPathResource imageRes = new ClassPathResource(imagePath);
            if (!imageRes.exists()) {
                return handleExceptionByMessage(notFoundMessage);
            }

            byte[] bytes = StreamUtils.copyToByteArray(imageRes.getInputStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(bytes);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

    public ResponseEntity<?> handleExceptionByMessage(String s) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), s);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

}
