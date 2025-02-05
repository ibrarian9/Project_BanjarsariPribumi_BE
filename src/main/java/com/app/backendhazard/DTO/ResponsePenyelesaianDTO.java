package com.app.backendhazard.DTO;

import com.app.backendhazard.Models.Department;
import lombok.Data;

@Data
public class ResponsePenyelesaianDTO {
    private String namaPenyelesaian;
    private Department department;
    private String linkGambar;
}
