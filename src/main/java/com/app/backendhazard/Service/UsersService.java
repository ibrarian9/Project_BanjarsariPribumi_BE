package com.app.backendhazard.Service;


import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.DTO.UpdateUserDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

import java.util.Map;

public interface UsersService {
    ResponseEntity<?> updateProfile(Long id, UpdateUserDTO updateUserDTO);
    ResponseEntity<?> login(Authentication authentication);
    ResponseEntity<Map<String, Object>> getDetailUser(Long id);
    ResponseEntity<Map<String, Object>> registerUser(RegisterDTO registerDTO);
}
