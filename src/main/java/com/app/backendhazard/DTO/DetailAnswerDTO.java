package com.app.backendhazard.DTO;

import com.app.backendhazard.Models.Status;
import lombok.Data;

@Data
public class DetailAnswerDTO {
    private Long id;
    private boolean jawaban;
    private String catatan;
    private String imageLink;
    private Status status;
    private String lastUpdate;
}
