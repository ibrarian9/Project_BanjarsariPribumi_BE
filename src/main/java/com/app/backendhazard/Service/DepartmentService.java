package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.DepartmentsDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface DepartmentService {
    ResponseEntity<Map<String, Object>> getAllDepartment();
    ResponseEntity<Map<String, Object>> getDetailDepartment(Long id);
    ResponseEntity<Map<String, Object>> addDepartment(DepartmentsDTO department);
}
