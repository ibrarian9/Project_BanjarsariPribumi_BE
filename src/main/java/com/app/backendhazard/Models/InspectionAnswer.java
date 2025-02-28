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
    @Column(name = "jawaban", nullable = false)
    private Boolean jawaban;
    @Column(name = "catatan")
    private String catatan;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;
    @ManyToOne
    @JoinColumn(name = "users_id", referencedColumnName = "id")
    private Users lastUpdate;
}
