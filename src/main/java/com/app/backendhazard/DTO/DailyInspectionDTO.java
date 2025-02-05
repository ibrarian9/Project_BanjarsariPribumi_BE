package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class DailyInspectionDTO {
    private String namaPengawas;
    private Long departmentPengawasId;
    private Long shiftKerjaId;
    private Long areaKerjaId;
    private Long statusLaporanId;
    private String keteranganAreaKerja;
}
