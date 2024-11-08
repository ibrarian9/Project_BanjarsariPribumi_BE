package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "pencapaian")
public class Pencapaian {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne
    @JoinColumn(name = "hazard_report_id")
    private HazardReport hazardReport;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @Column(name = "safety_talk", length = 50)
    private String safetyTalk;
}
