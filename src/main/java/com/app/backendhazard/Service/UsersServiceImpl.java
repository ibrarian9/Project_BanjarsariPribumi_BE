package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.DTO.UpdateUserDTO;
import com.app.backendhazard.Models.Roles;
import com.app.backendhazard.Models.StatusKaryawan;
import com.app.backendhazard.Models.Users;
import com.app.backendhazard.Repository.RoleRepository;
import com.app.backendhazard.Repository.StatusKaryawanRepository;
import com.app.backendhazard.Repository.UsersRepository;
import com.app.backendhazard.Response.LoginResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.catalina.User;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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
    private final Integer reject = 3;

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

    @Override
    public ResponseEntity<Map<String, Object>> updateUser(Long userId, RegisterDTO updateDTO) {
        //Find User By Id
        Optional<Users> userOptional = usersRepository.findById(userId);

        if (userOptional.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User Not Found"));
        }
        Users users = userOptional.get();

        //Update email and password
        if (updateDTO.getEmail() != null) {
            users.setEmail(updateDTO.getEmail());
        }
        if (updateDTO.getPassword() != null) {
            String encryptedPassword = passwordEncoder.encode(updateDTO.getPassword());
            users.setPassword(encryptedPassword);
        }
        //Save update
        usersRepository.save(users);
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("message", "User Updated Successfully");
        return ResponseEntity.ok(response);
    }

//    @Override
//    public ResponseEntity<Map<String, Object>> updateUser(Long userId, UpdateUserDTO updateUserDTO) {
//        Map<String, Object> response = new HashMap<>();
//        try {
//            Optional<Users> usersOptional = usersRepository.findById(userId);
//            if (usersOptional.isPresent()) {
//                Users user = usersOptional.get();
//                // Update hanya field yang ada di UpdateUserDTO
//                user.setUsername(updateUserDTO.getUsername());
//                user.setEmail(updateUserDTO.getEmail());
//                // Save update
//                usersRepository.save(user);
//                response.put("message", "User Updated Successfully");
//                response.put("user", user);
//                return ResponseEntity.ok(response);
//            } else {
//                response.put("message", "User not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
//            }
//        } catch (Exception e) {
//            response.put("message", "Error Updating User" + e.getMessage());
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
//        }
//    }

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
