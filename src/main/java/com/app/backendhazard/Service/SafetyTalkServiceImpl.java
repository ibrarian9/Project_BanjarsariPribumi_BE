package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.SafetyTalkDTO;
import com.app.backendhazard.Models.Department;
import com.app.backendhazard.Models.SafetyTalk;
import com.app.backendhazard.Models.Users;
import com.app.backendhazard.Repository.DepartmentRepository;
import com.app.backendhazard.Repository.SafetyTalkRepo;
import com.app.backendhazard.Repository.UsersRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class SafetyTalkServiceImpl implements SafetyTalkService {

    private final UsersRepository usersRepo;
    private final SafetyTalkRepo safetyTalkRepo;
    private final DepartmentRepository departmentRepo;
    private final ResponseHelperService responseHelperService;

    @Override
    public ResponseEntity<Map<String, Object>> getAllSafetyTalk() {
        return responseHelperService.getAllData(safetyTalkRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailSafetyTalk(Long id) {
        return responseHelperService.getDetailData(id, safetyTalkRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addSafetyTalk(SafetyTalkDTO safetyTalk) {

        Users users = usersRepo.findById(safetyTalk.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("User Not Found " + safetyTalk.getUserId()));

        Department department = departmentRepo.findById(safetyTalk.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found " + safetyTalk.getDepartmentId()));

        SafetyTalk newSafetyTalk = new SafetyTalk();
        newSafetyTalk.setDepartment(department);
        newSafetyTalk.setUser(users);
        newSafetyTalk.setAttaintmentNumber(safetyTalk.getAttainmentNum());
        newSafetyTalk.setTargetSafetyTalk(safetyTalk.getTargetNum());
        newSafetyTalk.setTanggal(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        safetyTalkRepo.save(newSafetyTalk);

        return responseHelperService.saveEntityWithMessage("Safety Talk Berhasil Ditambahkan");
    }

    @Override
    public ResponseEntity<?> exportToExcel() {
        List<SafetyTalk> data = safetyTalkRepo.findAll();

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Safety Talk");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("No");
            headerRow.createCell(1).setCellValue("Nama Karyawan");
            headerRow.createCell(2).setCellValue("Jabatan");
            headerRow.createCell(3).setCellValue("Pencapaian Safety Talk");
            headerRow.createCell(4).setCellValue("Target Safety Talk");

            int rowNum = 1;
            for (SafetyTalk safetyTalk : data) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                row.createCell(1).setCellValue(safetyTalk.getUser().getUsername());
                row.createCell(2).setCellValue(safetyTalk.getUser().getStatusKaryawan().getNamaStatus());
                row.createCell(3).setCellValue(safetyTalk.getAttaintmentNumber());
                row.createCell(4).setCellValue(safetyTalk.getTargetSafetyTalk());

                rowNum++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=safetyTalk_reports.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e){
            return responseHelperService.handleException(e);
        }
    }
}
