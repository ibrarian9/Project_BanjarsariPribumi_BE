package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.DepartmentsDTO;
import com.app.backendhazard.Models.Company;
import com.app.backendhazard.Models.Department;
import com.app.backendhazard.Repository.CompanyRepository;
import com.app.backendhazard.Repository.DepartmentRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@AllArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final CompanyRepository companyRepository;
    private final ResponseHelperService responseHelperService;

    @Override
    public ResponseEntity<Map<String, Object>> getAllDepartment() {
        return responseHelperService.getAllData(departmentRepository.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailDepartment(Long id) {
        return responseHelperService.getDetailData(id, departmentRepository);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addDepartment(DepartmentsDTO department) {
        Company company = companyRepository.findById(department.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        Department newDepartment = new Department();
        newDepartment.setNamaDepartment(department.getNamaDepartment());
        newDepartment.setCompany(company);

        return responseHelperService.saveEntity(newDepartment, departmentRepository);
    }
}
