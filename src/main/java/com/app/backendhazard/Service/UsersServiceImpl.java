package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.Models.Roles;
import com.app.backendhazard.Models.StatusKaryawan;
import com.app.backendhazard.Models.Users;
import com.app.backendhazard.Repository.RoleRepository;
import com.app.backendhazard.Repository.StatusKaryawanRepository;
import com.app.backendhazard.Repository.UsersRepository;
import com.app.backendhazard.Response.LoginResponse;
import lombok.AllArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final StatusKaryawanRepository statusKaryawanRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public ResponseEntity<?> login(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtService.generateJwtToken(authentication);
        LoginResponse loginResponse = getLoginResponse(jwt);
        // Set Response Api
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", loginResponse);
        response.put("message", "Login Success");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> registerUser(RegisterDTO registerDTO) {

        if (usersRepository.findByUsernameOrEmail(registerDTO.getUsername(), registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        StatusKaryawan statusKaryawan = statusKaryawanRepository.findById(registerDTO.getStatusKaryawanId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status Karyawan ID"));

        Roles roles = roleRepository.findById(registerDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));

        // Set Users
        Users users = new Users();
        users.setUsername(registerDTO.getUsername());
        users.setNik(registerDTO.getNik());
        users.setEmail(registerDTO.getEmail());
        users.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        users.setStatusKaryawan(statusKaryawan);
        users.setRole(roles);

        // Set Response
        usersRepository.save(users);
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("message", "User Registered Successfully");
        return ResponseEntity.ok(response);
    }

    private static @NotNull LoginResponse getLoginResponse(String jwt) {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // Set Login Response
        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(jwt);
        loginResponse.setUsername(userDetails.getUsername());
        loginResponse.setNik(userDetails.getUser().getNik());
        loginResponse.setEmail(userDetails.getUser().getEmail());
        loginResponse.setRole(userDetails.getUser().getRole().getNamaRole());
        return loginResponse;
    }

}
