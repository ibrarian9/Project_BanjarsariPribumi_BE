package com.app.backendhazard.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class AnswerDTO {
    private Long inspection_question_id;
    private Boolean jawaban;
    private String catatan;
    private String gambar;
    private Long detailDailyInspectionId;
}
