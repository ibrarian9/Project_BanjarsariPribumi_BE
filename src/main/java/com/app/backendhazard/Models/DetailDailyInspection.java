package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "detail_daily_inspection",
    indexes = {
        @Index(name = "idx_detail_daily_inspection_di_id", columnList = "daily_inspection_id")
    }
)
public class DetailDailyInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "daily_inspection_id")
    private DailyInspection dailyInspection;
    @ManyToOne
    @JoinColumn(name = "inspection_question", referencedColumnName = "id")
    private InspectionQuestion inspectionQuestion;
    @ManyToOne
    @JoinColumn(name = "inspection_answer", referencedColumnName = "id")
    private InspectionAnswer inspectionAnswer;
}
