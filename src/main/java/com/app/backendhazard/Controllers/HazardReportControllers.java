package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.HazardReportDTO;
import com.app.backendhazard.DTO.HazardStatusDTO;
import com.app.backendhazard.Service.HazardReportService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class HazardReportControllers {

    private final HazardReportService hazardReportService;

    @GetMapping(path = "/historyStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> searchStatusHistory(
            @RequestParam(value = "q", required = false) String search,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        return hazardReportService.searchAllHistoryStatus(search, size, page);
    }

    @GetMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailHazardStatusHistory(@PathVariable Long id, HttpServletRequest request) {
        return hazardReportService.getDetailHistoryStatus(id, request);
    }

    @PutMapping(path = "/historyStatus/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editHazardStatusHistory(@PathVariable Long id, @RequestBody HazardStatusDTO hazardStatusDTO) {
        return hazardReportService.editHistoryStatus(id, hazardStatusDTO);
    }

    @DeleteMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHazardStatusHistory(@PathVariable Long id) {
        return hazardReportService.deleteHistoryStatus(id);
    }

    @GetMapping(path = "/historyStatus/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filterSearchHistory(
            @RequestParam(value = "dept", required = false) String dept,
            @RequestParam(value = "status", required = false) String status) {
        return hazardReportService.filterAllHistoryStatus(dept, status);
    }

    @GetMapping(path = "/hazardReport/filter")
    public ResponseEntity<?> filterHazardReport(
            @RequestParam(required = false) Long deptId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) Long statusId,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "0") Integer page
    ) {
        return hazardReportService.getFilterReport(deptId, startDate, endDate, statusId, page, size);
    }

    @PostMapping(path = "/hazardReport/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addHazardReport(
            @ModelAttribute("hazardReport") HazardReportDTO hazardReport,
            @RequestPart(value = "gambar") MultipartFile gambar) {
        return hazardReportService.addHazardReport(hazardReport, gambar);
    }

    @GetMapping(path = "/gambar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> gambarById(@PathVariable Long id) {
        return hazardReportService.imageForHazardReport(id);
    }

    @GetMapping(value = "/historyStatus/export")
    public ResponseEntity<?> exportStatusHistory() {
        return hazardReportService.exportToExcel();
    }
}
