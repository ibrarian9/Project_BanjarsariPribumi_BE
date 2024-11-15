package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class RegisterDTO {
    private String username;
    private String nik;
    private String email;
    private String password;
    private String confirmPassword;
    private Long statusKaryawanId;
    private Long roleId;
}
