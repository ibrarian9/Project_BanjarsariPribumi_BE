package com.app.backendhazard.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "penyelesaian")
public class Penyelesaian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_penyelesaian")
    private String namaPenyelesaian;
    @ManyToOne
    @JoinColumn(name = "department_id", referencedColumnName = "id")
    private Department department;
    @Lob
    @JsonIgnore
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;
}
