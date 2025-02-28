package com.app.backendhazard.DTO;

import lombok.Data;

import java.util.List;

@Data
public class InspectionRequestDTO {
    private DailyInspectionDTO dailyInspectionDTO;
    private List<AnswerDTO> answerDTOList;
    private List<Integer> imageIndicates;
}
