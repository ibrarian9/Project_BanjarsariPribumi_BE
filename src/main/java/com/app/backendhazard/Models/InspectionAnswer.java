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
    @ManyToOne
    @JoinColumn(name = "daily_detail_inspection")
    private DetailDailyInspection detailDailyInspection;
    @ManyToOne
    @JoinColumn(name = "inspection_question_id")
    private InspectionQuestion inspectionQuestion;
    @Column(name = "jawaban")
    private Boolean jawaban;
    @Column(name = "catatan")
    private String catatan;
    @Column(name = "gambar", columnDefinition = "TEXT")
    private String gambar;


}
