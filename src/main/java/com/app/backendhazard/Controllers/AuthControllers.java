package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.LoginDTO;
import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.Response.ErrorResponse;
import com.app.backendhazard.Service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/auth")
public class AuthControllers {

    private final AuthenticationManager authenticationManager;
    private final UsersService usersService;

    @PostMapping(value = "/register", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerUser(@RequestBody RegisterDTO registerDTO) {
        try {
            usersService.registerUser(registerDTO);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDTO.getEmail(), loginDTO.getPassword()
            ));
            return usersService.login(authentication);
        } catch (BadCredentialsException e) {
            ErrorResponse errRes = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errRes);
        } catch (Exception e) {
            ErrorResponse errRes = new ErrorResponse(HttpStatus.NOT_FOUND.value(), e.getLocalizedMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errRes);
        }
    }

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }
}

