package com.app.backendhazard.Models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "safety_talk")
public class SafetyTalk {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users user;
    @Column(name = "pencapaian", length = 2)
    private Long attaintmentNumber;
    @Column(name = "target", length = 2)
    private Long targetNumber;
    @Column(name = "tanggal")
    private LocalDateTime tanggal;
}
