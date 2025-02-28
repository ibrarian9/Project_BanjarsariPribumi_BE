package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class HazardReportDTO {
    private String title;
    private String namaPelapor;
    private Long kategoriTemuanId;
    private String lokasi;
    private String deskripsi;
    private Long departmentPelaporId;
    private Long departmentPerbaikanId;
    private Long statusLaporanId;
    private String tindakan;
    private Long userId;
}
