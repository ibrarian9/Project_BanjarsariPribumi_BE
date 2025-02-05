package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.CompanyDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface CompanyService {
    ResponseEntity<Map<String, Object>> getAllCompany();
    ResponseEntity<Map<String, Object>> getDetailCompany(Long id);
    ResponseEntity<Map<String, Object>> addCompany(CompanyDTO company);
}
