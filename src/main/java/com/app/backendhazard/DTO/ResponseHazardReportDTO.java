package com.app.backendhazard.DTO;

import com.app.backendhazard.Models.Department;
import com.app.backendhazard.Models.KategoriTemuan;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseHazardReportDTO {
    private Long id;
    private String title;
    private String namaPelapor;
    private String lokasi;
    private String deskripsi;
    private KategoriTemuan kategoriTemuan;
    private Department departmentPelapor;
    private Department departmentPerbaikan;
    private ResponsePenyelesaianDTO penyelesaian;
    private String tindakan;
    private LocalDateTime tanggalKejadian;
    private String linkGambar;
}
