package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Data
@Table(name = "inspection_question")
public class InspectionQuestion {
    @Getter
    @Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "area_kerja_id")
    private AreaKerja areaKerja;
    @Column(name = "question")
    private String question;
}
