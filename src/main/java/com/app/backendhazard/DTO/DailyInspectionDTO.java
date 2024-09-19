package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class DailyInspectionDTO {
    private String namaPengawas;
    private Long departmentPengawasId;
    private String tanggalInspeksi;
    private Long shiftKerjaId;
    private Long areaKerjaId;
    private String keteranganAreaKerja;
}
