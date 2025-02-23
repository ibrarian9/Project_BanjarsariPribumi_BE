package com.app.backendhazard.Controllers;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/version")
public class VersionControllers {
    private static final String LATEST_VERSION = "1.2";

    @GetMapping(path = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Map<String, String>> getLatestVersion() {
        Map<String, String> response = new HashMap<>();
        response.put("latest_version", LATEST_VERSION);
        return ResponseEntity.ok(response);
    }
}
