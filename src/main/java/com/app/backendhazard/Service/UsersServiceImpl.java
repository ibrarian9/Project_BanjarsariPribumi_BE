package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.DTO.UpdateUserDTO;
import com.app.backendhazard.Models.Nik;
import com.app.backendhazard.Models.Roles;
import com.app.backendhazard.Models.StatusKaryawan;
import com.app.backendhazard.Models.Users;
import com.app.backendhazard.Repository.NikRepository;
import com.app.backendhazard.Repository.RoleRepository;
import com.app.backendhazard.Repository.StatusKaryawanRepository;
import com.app.backendhazard.Repository.UsersRepository;
import com.app.backendhazard.Response.LoginResponse;
import jakarta.persistence.EntityNotFoundException;
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
import java.util.Optional;

@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final JwtService jwtService;
    private final UsersRepository usersRepository;
    private final StatusKaryawanRepository statusKaryawanRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponseHelperService responseHelperService;
    private final NikRepository nikRepository;

    @Override
    public ResponseEntity<?> updateProfile(Long id, UpdateUserDTO updateUserDTO) {
        Users users = usersRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + id));

        // Update username if provided
        if (updateUserDTO.getUsername() != null && !updateUserDTO.getUsername().isEmpty()) {
            users.setUsername(updateUserDTO.getUsername());
        }

        // Update email if provided and not duplicate
        if (updateUserDTO.getEmail() != null && !updateUserDTO.getEmail().isEmpty()) {
            if (!users.getEmail().equals(updateUserDTO.getEmail()) && usersRepository.existsByEmail(updateUserDTO.getEmail())) {
                throw new IllegalArgumentException("Email is already in use: " + updateUserDTO.getEmail());
            }
            users.setEmail(updateUserDTO.getEmail());
        }

        // Update password if provided
        if (updateUserDTO.getPassword() != null && !updateUserDTO.getPassword().isEmpty()) {
            users.setPassword(passwordEncoder.encode(updateUserDTO.getPassword()));
        }

        usersRepository.save(users);
        return responseHelperService.saveEntityWithMessage("Update Profile Berhasil!");
    }

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
    public ResponseEntity<Map<String, Object>> getDetailUser(Long id) {
        return responseHelperService.getDetailData(id, usersRepository);
    }

    @Override
    public ResponseEntity<Map<String, Object>> registerUser(RegisterDTO registerDTO) {

        if (usersRepository.findByUsernameOrEmail(registerDTO.getUsername(), registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        // Confirm if the provided NIK exists in the database
        Optional<Nik> nikEntity = nikRepository.findNikByDataNik(registerDTO.getNik());
        if (nikEntity.isEmpty()) {
            throw new IllegalArgumentException("NIK does not exist in the database");
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
        users.setStatusAktif(1);
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
        loginResponse.setId(userDetails.getUser().getId());
        loginResponse.setToken(jwt);
        loginResponse.setUsername(userDetails.getUsername());
        loginResponse.setNik(userDetails.getUser().getNik());
        loginResponse.setEmail(userDetails.getUser().getEmail());
        loginResponse.setRole(userDetails.getUser().getRole().getNamaRole());
        return loginResponse;
    }

}
