package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "inspection")
public class Inspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "daily_inspection_id", referencedColumnName = "id")
    private DailyInspection dailyInspection;
    @ManyToOne
    @JoinColumn(name = "inspection_question_id", referencedColumnName = "id")
    private InspectionQuestion inspectionQuestion;
    @Column(name = "answer")
    private Integer answer;
    @Column(name = "catatan")
    private String catatan;
    @Column(columnDefinition = "TEXT")
    private String gambar;
}
