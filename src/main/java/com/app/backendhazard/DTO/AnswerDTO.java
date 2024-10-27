package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class AnswerDTO {
    private Long questionId;
    private Boolean jawaban;
    private String catatan;
    private String gambar;
}
