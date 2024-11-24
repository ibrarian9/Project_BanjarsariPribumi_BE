package com.app.backendhazard.Service;

import com.app.backendhazard.Repository.*;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class SystemServiceImpl implements SystemService {

    private final UsersRepository usersRepo;
    private final ShiftRepository shiftRepo;
    private final StatusRepository statusRepo;
    private final AreaKerjaRepository areaKerjaRepo;
    private final StatusKaryawanRepository statusKaryawanRepo;
    private final KategoriTemuanRepository kategoriTemuanRepo;
    private final ResponseHelperService responseHelperService;
    private final StatusCompanyRepository statusCompanyRepo;

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatusKaryawan() {
        return responseHelperService.getAllData(statusKaryawanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatusCompany() {
        return responseHelperService.getAllData(statusCompanyRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatus() {
        return responseHelperService.getAllData(statusRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllUser() {
        return responseHelperService.getAllData(usersRepo.findByRoleId(2L));
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllWorkArea() {
        return responseHelperService.getAllData(areaKerjaRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllShift() {
        return responseHelperService.getAllData(shiftRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllFindings() {
        return responseHelperService.getAllData(kategoriTemuanRepo.findAll());
    }

}
