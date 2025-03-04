package com.app.backendhazard.Models;

import com.app.backendhazard.DTO.QuestionAnswerDTO;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "daily_inspection",
    indexes = {
        @Index(name = "idx_daily_inspection_id", columnList = "id")
    }
)
public class DailyInspection {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "nama_pengawas")
    private String namaPengawas;
    @Column(name = "tanggal_inspeksi")
    private LocalDateTime tanggalInspeksi;
    @ManyToOne
    @JoinColumn(name = "department_pengawas_id", referencedColumnName = "id")
    private Department departmentPengawas;
    @ManyToOne
    @JoinColumn(name = "shift_kerja_id", referencedColumnName = "id")
    private Shift shiftKerja;
    @ManyToOne
    @JoinColumn(name = "area_kerja_id", referencedColumnName = "id")
    private AreaKerja areaKerja;
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private Users user;
    @Column(name = "keterangan_area_kerja")
    private String keteranganAreaKerja;
    @ManyToOne
    @JoinColumn(name = "status_id", referencedColumnName = "id")
    private Status status;
    @Column(name = "alasan")
    private String alasan;
    @Column(name = "updateBy")
    private String updateBy;
    @Transient
    private List<QuestionAnswerDTO> detailQuestionAnswers;
}
