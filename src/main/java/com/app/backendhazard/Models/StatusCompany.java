package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "status_company")
public class StatusCompany {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_status")
    private String namaStatus;
}
