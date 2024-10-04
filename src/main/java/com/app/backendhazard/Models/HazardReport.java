package com.app.backendhazard.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "hazard_report")
@Data
public class HazardReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @Column(name = "title")
    private String title;
    @Column(name = "nama_pelapor")
    private String namaPelapor;
    @Column(name = "lokasi")
    private String lokasi;
    @Column(columnDefinition = "TEXT")
    private String deskripsi;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id", nullable = false)
    private Status status;
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
    @Column(name = "alasan")
    private String alasan;
    @Lob
    @JsonIgnore
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;
    @Transient
    @JsonProperty("linkGambar")
    public String getLinkGambar() {
        return "http://192.168.1.14:8080/api/gambar/" + id;
    }
    @Transient
    public String imagePath(){
        if (gambar == null) return null;
        return "upload/" + id + "/" + gambar;
    }
}