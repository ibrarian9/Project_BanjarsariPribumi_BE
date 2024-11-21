package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.RegisterDTO;
import com.app.backendhazard.DTO.UpdateUserDTO;
import com.app.backendhazard.Response.ErrorResponse;
import com.app.backendhazard.Service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.*;

@AllArgsConstructor
@RestController
@RequestMapping("api/users")
public class UsersControllers {

    private final AuthenticationManager authenticationManager;
    private final UsersService usersService;
//
//    @PutMapping(value = "/update/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody RegisterDTO registerDTO) {
//        try {
//            return usersService.updateUser(id, registerDTO);
//        } catch (Exception e) {
//            return handleException(e);
//        }
//    }

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }
}
