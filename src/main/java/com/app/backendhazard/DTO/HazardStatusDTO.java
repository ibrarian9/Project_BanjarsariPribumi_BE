package com.app.backendhazard.DTO;

import lombok.Data;

@Data
public class HazardStatusDTO {
    private Long reportId;
    private Long statusId;
    private String alasan;
    private String updateBy;
}
