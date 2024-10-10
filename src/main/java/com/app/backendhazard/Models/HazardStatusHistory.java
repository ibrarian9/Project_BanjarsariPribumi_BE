package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "hazard_history_status")
@Data
public class HazardStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "report_id", nullable = false, referencedColumnName = "id", unique = true)
    private HazardReport report;
    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false, referencedColumnName = "id")
    private Status status;
    @Column(name = "alasan")
    private String alasan;
    @Column(name = "update_by", nullable = false)
    private String updateBy;
    @Column(name = "update_date", nullable = false)
    private LocalDateTime updateDate;
}
