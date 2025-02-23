package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.CompanyDTO;
import com.app.backendhazard.Models.Company;
import com.app.backendhazard.Models.StatusCompany;
import com.app.backendhazard.Repository.CompanyRepository;
import com.app.backendhazard.Repository.StatusCompanyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@AllArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository perusahaanRepo;
    private final StatusCompanyRepository statusRepo;
    private final ResponseHelperService responseHelperService;

    @Override
    public ResponseEntity<Map<String, Object>> getAllCompany() {
        return responseHelperService.getAllData(perusahaanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailCompany(Long id) {
        return responseHelperService.getDetailData(id, perusahaanRepo);
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> addCompany(CompanyDTO company) {
        StatusCompany statusCompany = statusRepo.findById(company.getStatusCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found"));

        Company newCompany = new Company();
        newCompany.setNamaCompany(company.getNamaCompany());
        newCompany.setStatusCompany(statusCompany);

        return responseHelperService.saveEntity(newCompany, perusahaanRepo);
    }
}
