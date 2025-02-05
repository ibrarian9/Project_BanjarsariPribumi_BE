package com.app.backendhazard.Response;

import lombok.Data;

@Data
public class LoginResponse {
    private String token;
    private String username;
    private String nik;
    private String email;
    private String role;
    private Long id;
}
