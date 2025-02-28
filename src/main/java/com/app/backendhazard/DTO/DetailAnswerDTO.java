package com.app.backendhazard.DTO;

import com.app.backendhazard.Models.Status;
import com.app.backendhazard.Models.Users;
import lombok.Data;

@Data
public class DetailAnswerDTO {
    private Long id;
    private boolean jawaban;
    private String catatan;
    private String imageLink;
    private Status status;
    private Users lastUpdate;
}
