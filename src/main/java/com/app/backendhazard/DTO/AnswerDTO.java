package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long inspectionQuestionId;
    private Boolean jawaban;
    private String catatan;
    private String gambar;
    private Long detailDailyInspectionId;
}
