package com.app.backendhazard.Service;

import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SystemService {
    ResponseEntity<Map<String, Object>> getSystemVersion();
    ResponseEntity<Map<String, Object>> getAllUser();
    ResponseEntity<Map<String, Object>> getAllShift();
    ResponseEntity<Map<String, Object>> getAllStatus();
    ResponseEntity<Map<String, Object>> getAllFindings();
    ResponseEntity<Map<String, Object>> getAllWorkArea();
    ResponseEntity<Map<String, Object>> getAllStatusKaryawan();
    ResponseEntity<Map<String, Object>> getAllStatusCompany();
}
