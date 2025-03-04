package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.DailyInspectionStatusDTO;
import com.app.backendhazard.DTO.DetailInspectionDTO;
import com.app.backendhazard.DTO.InspectionRequestDTO;
import com.app.backendhazard.DTO.UpdateInspectionStatusDTO;
import com.app.backendhazard.Service.DailyInspectionService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class DailyInspectionControllers {

    private final DailyInspectionService dailyInspectionService;

    @GetMapping(path = "/inspectionQuestion/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {
        return dailyInspectionService.getInspectionQuestion(id);
    }

    @PostMapping(path = "/inspectionAnswer/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAnswer(
            @RequestPart("requestDTO") InspectionRequestDTO requestDTO,
            @RequestParam(value = "image", required = false) List<MultipartFile> imageFiles
    ) {
        List<MultipartFile> nonEmptyImageFiles = imageFiles != null ?
                imageFiles.stream().filter(file -> !file.isEmpty()).toList() :
                new ArrayList<>();

        return dailyInspectionService.addInspectionAnswer(requestDTO, nonEmptyImageFiles);
    }

    @PutMapping(path = "/inspection/status/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editStatusDailyInspection(@PathVariable Long id,@RequestBody DailyInspectionStatusDTO dailyInspectionStatusDTO) {
        return dailyInspectionService.editStatusDailyInspection(id, dailyInspectionStatusDTO);
    }

    @PutMapping(path = "/inspectionAnswer/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editInspectionAnswer(
            @PathVariable Long id,
            @RequestBody UpdateInspectionStatusDTO updateInspectionStatusDTO
    ) {
        return dailyInspectionService.editStatusAnswer(id, updateInspectionStatusDTO);
    }

    @PostMapping(path = "/detailDailyInspection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postDetailDaily(@RequestBody DetailInspectionDTO detailInspectionDTO) {
        return dailyInspectionService.addDetailDailyInspection(detailInspectionDTO);
    }

    @GetMapping(path = "/dailyInspection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDailyInspection(
            @RequestParam(value = "q", required = false) String search,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "page", required = false) Integer page
    ) {
        return dailyInspectionService.getAllDailyInspection(search, size, page);
    }

    @GetMapping(path = "/dailyInspection/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailDailyInspection(@PathVariable Long id, HttpServletRequest request) {
        return dailyInspectionService.getDetailInspectionAnswer(id, request);
    }

    @GetMapping(path = "/dailyInspection/export")
    public ResponseEntity<?> exportDailyInspection() {
        return dailyInspectionService.exportToExcel();
    }

    @DeleteMapping(path = "/dailyInspection/{id}")
    public ResponseEntity<?> deleteDailyInspection(@PathVariable Long id) {
        return dailyInspectionService.deleteDailyInspection(id);
    }

    @GetMapping(path = "/imageDailyInspection/{dailyInspectionId}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inpectionImage(@PathVariable Long dailyInspectionId, @PathVariable Long id) {
        return dailyInspectionService.imageForInspection(dailyInspectionId, id);
    }
}
