package com.app.backendhazard.Controllers;

import com.app.backendhazard.Service.SystemService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class DropdownControllers {

    private final SystemService systemService;

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        return systemService.getAllUser();
    }

    @GetMapping(value = "/workArea", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllWorkArea() {
        return systemService.getAllWorkArea();
    }

    @GetMapping(value = "/workShift", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllWorkShift() {
        return systemService.getAllShift();
    }

    @GetMapping(path = "/statusStaff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatusStaff(){
        return systemService.getAllStatusKaryawan();
    }

    @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStatus(){
        return systemService.getAllStatus();
    }

    @GetMapping(value = "/findings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllFindings() {
        return systemService.getAllFindings();
    }

    @GetMapping(value = "/statusCompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStatusCompany() {
        return systemService.getAllStatusCompany();
    }
}
