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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
        newSafetyTalk.setTanggal(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        safetyTalkRepo.save(newSafetyTalk);

        return responseHelperService.saveEntityWithMessage("Safety Talk Berhasil Ditambahkan");
    }
}
