package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "kategori_temuan")
public class KategoriTemuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_kategori_temuan")
    private String kategoriTemuan;
}
