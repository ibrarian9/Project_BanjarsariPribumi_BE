package com.app.backendhazard.DTO;

import com.app.backendhazard.Models.Status;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class HistoryStatusDTO {
    Long id;
    ResponseHazardReportDTO report;
    Status status;
    String alasan;
    String updateBy;
    LocalDateTime updateDate;
}
