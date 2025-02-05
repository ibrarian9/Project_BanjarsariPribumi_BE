package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "company", uniqueConstraints = @UniqueConstraint(columnNames = {"nama_perusahaan"}))
public class Company {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "status_perusahaan_id")
    private StatusCompany statusCompany;
    @Column(name = "nama_perusahaan")
    private String namaCompany;
}
