package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Models.Pencapaian;
import com.app.backendhazard.Response.ErrorResponse;
import com.app.backendhazard.Service.SystemService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class SystemControllers {

    private final long twoMb = 2 * 1024 * 1024;
    private final SystemService systemService;

    @GetMapping(path = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCompany() {
        try {
            return systemService.getAllCompany();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/company/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailCompany(@PathVariable long id) {
        try {
            return systemService.getDetailCompany(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/company/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO company) {
        try {
            return systemService.addCompany(company);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDepartment(){
        try {
            return systemService.getAllDepartment();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/departments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailDepartment(@PathVariable long id){
        try {
            return systemService.getDetailDepartment(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/departments/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDepartments(@RequestBody DepartmentsDTO department){
        try {
            return systemService.addDepartment(department);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/hazardReport", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllHazard(){
        try {
            return systemService.getAllHazardReport();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/hazardReport/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailHazard(@PathVariable Long id){
        try {
            return systemService.getDetailedHazardReport(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/hazardReport/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> addHazardReport(@ModelAttribute("hazardReport") HazardReportDTO hazardReport,
                                             @RequestPart(value = "gambar") MultipartFile gambar)
    {
        if (gambar.getSize() > twoMb) {
            return handleLimitImage();
        }

        try {
            return systemService.addHazardReport(hazardReport, gambar);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @DeleteMapping(path = "/hazardReport/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHazardReport(@PathVariable Long id) {
        try {
            return systemService.deleteHazardReport(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/gambar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> gambarById(@PathVariable Long id) {
        return systemService.imageForHazardReport(id);
    }

    @GetMapping(path = "/resolutionImage/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolutionImage(@PathVariable Long id) {
        return systemService.imageForResolution(id);
    }

    @GetMapping(path = "/inspection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllInspection() {
        try {
            return systemService.getAllInspection();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/inspection/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailInspection(@PathVariable Long id) {
        try {
            return systemService.getDetailInspection(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/pencapaian", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postPencapaian(@RequestBody Pencapaian pencapaian){
        try {
            return systemService.addPencapaian(pencapaian);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/resolution/{id}/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postPenyelesaian(@PathVariable Long id
            ,@ModelAttribute("penyelesaian") PenyelesaianDTO penyelesaian
            ,@RequestPart(value = "gambar") MultipartFile gambar)
    {

        if (gambar.getSize() > twoMb){
            return handleLimitImage();
        }

        try {
            return systemService.addPenyelesaian(id, penyelesaian, gambar);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/safetyTalk", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSafetyTalk(){
        try {
            return systemService.getAllSafetyTalk();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/safetyTalk/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailSafetyTalk(@PathVariable Long id){
        try {
            return systemService.getDetailSafetyTalk(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/safetyTalk/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSafetyTalk(@RequestBody SafetyTalkDTO safetyTalk){
        try {
            return systemService.addSafetyTalk(safetyTalk);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/statusStaff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatusStaff(){
        try {
            return systemService.getAllStatusKaryawan();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/statusStaff/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailStatusStaff(@PathVariable Long id){
        try {
            return systemService.getDetailStatusKaryawan(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStatus(){
        try {
            return systemService.getAllStatus();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/historyStatus", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllHazardStatusHistory(){
        try {
            return systemService.getAllHistoryStatus();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailHazardStatusHistory(@PathVariable Long id){
        try {
            return systemService.getDetailHistoryStatus(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PutMapping(path = "/historyStatus/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editHazardStatusHistory(@PathVariable Long id, @RequestBody HazardStatusDTO hazardStatusDTO){
        try {
            return systemService.editHistoryStatus(id, hazardStatusDTO);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @DeleteMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHazardStatusHistory(@PathVariable Long id){
        try {
            return systemService.deleteHistoryStatus(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        try {
            return systemService.getAllUser();
        } catch (Exception e){
            return handleException(e);
        }
    }

    public ResponseEntity<?> handleLimitImage() {
        ErrorResponse errorResponse = new ErrorResponse(HttpStatus.EXPECTATION_FAILED.value(), "File Too Large!");
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(errorResponse);
    }

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

}
