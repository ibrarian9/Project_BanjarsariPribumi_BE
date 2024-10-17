package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name = "area_kerja")
public class AreaKerja {
    //Getter and Setter
    @Setter
    @Getter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_area_kerja")
    private String namaAreaKerja;
}

