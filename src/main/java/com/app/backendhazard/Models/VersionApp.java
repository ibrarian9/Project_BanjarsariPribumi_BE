package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "version_app")
public class VersionApp {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "version", length = 50, nullable = false)
    private String version;
}
