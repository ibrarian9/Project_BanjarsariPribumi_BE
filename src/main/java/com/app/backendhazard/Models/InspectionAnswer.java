package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inspection_answer")
public class InspectionAnswer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "jawaban")
    private Boolean jawaban;
    @Column(name = "catatan")
    private String catatan;
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;


}
