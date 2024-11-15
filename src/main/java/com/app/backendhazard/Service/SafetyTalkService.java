package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.SafetyTalkDTO;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface SafetyTalkService {
    ResponseEntity<Map<String, Object>> getAllSafetyTalk();
    ResponseEntity<Map<String, Object>> getDetailSafetyTalk(Long id);
    ResponseEntity<Map<String, Object>> addSafetyTalk(SafetyTalkDTO safetyTalk);
}
