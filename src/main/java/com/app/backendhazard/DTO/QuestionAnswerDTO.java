package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class QuestionAnswerDTO {
    private String questionText;
    private DetailAnswerDTO answerDetail;
}
