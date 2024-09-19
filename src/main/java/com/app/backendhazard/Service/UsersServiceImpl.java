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
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return ResponseEntity.ok(new LoginResponse(
                jwt,
                userDetails.getUsername(),
                userDetails.getUser().getNik(),
                userDetails.getUser().getEmail(),
                userDetails.getUser().getRole().getNamaRole()
        ));
    }

    @Override
    public void registerUser(RegisterDTO registerDTO) {

        if (usersRepository.findByUsernameOrEmail(registerDTO.getUsername(), registerDTO.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already exists");
        }

        StatusKaryawan statusKaryawan = statusKaryawanRepository.findById(registerDTO.getStatusKaryawanId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid status Karyawan ID"));

        Roles roles = roleRepository.findById(registerDTO.getRoleId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid role ID"));

        Users users = new Users(
                registerDTO.getUsername(),
                registerDTO.getNik(),
                registerDTO.getEmail(),
                passwordEncoder.encode(registerDTO.getPassword()),
                statusKaryawan,
                roles
        );

        usersRepository.save(users);
    }

}
