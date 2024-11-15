package com.app.backendhazard.Service;


import com.app.backendhazard.DTO.RegisterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UsersService {
    ResponseEntity<?> login(Authentication authentication);
    ResponseEntity<Map<String, Object>> registerUser(RegisterDTO registerDTO);
}
