package com.app.backendhazard.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "hazard_report")
@Data
public class HazardReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title")
    private String title;
    @Column(name = "nama_pelapor")
    private String namaPelapor;
    @Column(name = "lokasi")
    private String lokasi;
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    @ManyToOne
    @JoinColumn(name = "kategori_temuan_id", referencedColumnName = "id", nullable = false)
    private KategoriTemuan kategoriTemuan;
    @ManyToOne
    @JoinColumn(name = "department_pelapor_id", referencedColumnName = "id", nullable = false)
    private Department departmentPelapor;
    @ManyToOne
    @JoinColumn(name = "department_perbaikan_id", referencedColumnName = "id", nullable = false)
    private Department departmentPerbaikan;
    @ManyToOne
    @JoinColumn(name = "penyelesaian_id", referencedColumnName = "id")
    private Penyelesaian penyelesaian;
    @Column(name = "tindakan")
    private String tindakan;
    @Column(name = "tanggal_kejadian")
    private LocalDateTime tanggalKejadian;
    @Lob
    @JsonIgnore
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;
}