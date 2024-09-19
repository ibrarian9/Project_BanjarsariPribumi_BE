package com.app.backendhazard.Service;


import com.app.backendhazard.DTO.RegisterDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;

public interface UsersService {
    ResponseEntity<?> login(Authentication authentication);
    void registerUser(RegisterDTO registerDTO);
}
