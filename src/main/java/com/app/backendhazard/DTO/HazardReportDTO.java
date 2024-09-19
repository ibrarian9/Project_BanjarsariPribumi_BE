package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class HazardReportDTO {
    private String title;
    private String namaPelapor;
    private String lokasi;
    private String deskripsi;
    private Long statusId;
    private Long departmentPelaporId;
    private Long departmentPerbaikanId;
    private String tindakan;
}
