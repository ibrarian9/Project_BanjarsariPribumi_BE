package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Models.Pencapaian;
import com.app.backendhazard.Response.ErrorResponse;
import com.app.backendhazard.Service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class SystemControllers {

    private static final Logger log = LoggerFactory.getLogger(SystemControllers.class);
    private final long twoMb = 10 * 1024 * 1024;
    private final SystemService systemService;
    private final UsersService usersService;
    private final CompanyService companyService;
    private final SafetyTalkService safetyTalkService;
    private final PencapaianService pencapaianService;
    private final DepartmentService departmentService;
    private final HazardReportService hazardReportService;
    private final DailyInspectionService dailyInspectionService;

    @GetMapping(path = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCompany() {
        try {
            return companyService.getAllCompany();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/company/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailCompany(@PathVariable long id) {
        try {
            return companyService.getDetailCompany(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/company/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO company) {
        try {
            return companyService.addCompany(company);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDepartment(){
        try {
            return departmentService.getAllDepartment();
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/departments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailDepartment(@PathVariable long id){
        try {
            return departmentService.getDetailDepartment(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/departments/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDepartments(@RequestBody DepartmentsDTO department){
        try {
            return departmentService.addDepartment(department);
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
            return hazardReportService.addHazardReport(hazardReport, gambar);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/gambar/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> gambarById(@PathVariable Long id) {
        return hazardReportService.imageForHazardReport(id);
    }

    @GetMapping(path = "/resolutionImage/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolutionImage(@PathVariable Long id) {
        return pencapaianService.imageForResolution(id);
    }

    @GetMapping(path = "/inspectionQuestion/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getQuestions(@PathVariable Long id) {
        try {
            return dailyInspectionService.getInspectionQuestion(id);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping(path = "/inspectionAnswer/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postAnswer(
            @RequestPart("requestDTO") InspectionRequestDTO requestDTO,
            @RequestParam("image") List<MultipartFile> imageFiles
    ) {
        try {
            return dailyInspectionService.addInspectionAnswer(requestDTO, imageFiles);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PutMapping(path = "/inspection/status/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editStatusDailyInspection(@PathVariable Long id,@RequestBody DailyInspectionStatusDTO dailyInspectionStatusDTO) {
        try {
            return dailyInspectionService.editStatusDailyInspection(id, dailyInspectionStatusDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @PostMapping(path = "/detailDailyInspection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postDetailDaily(@RequestBody DetailInspectionDTO detailInspectionDTO) {
        try {
            return dailyInspectionService.addDetailDailyInspection(detailInspectionDTO);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    @GetMapping(path = "/dailyInspection", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDailyInspection(
            @RequestParam(value = "q", required = false) String search
    ) {
        try {
            return dailyInspectionService.getAllDailyInspection(search);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/dailyInspection/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailDailyInspection(@PathVariable Long id, HttpServletRequest request) {
        try {
            return dailyInspectionService.getDetailInspectionAnswer(id, request);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/dailyInspection/export")
    public ResponseEntity<?> exportDailyInspection() {
        return dailyInspectionService.exportToExcel();
    }

    @GetMapping(path = "/imageDailyInspection/{dailyInspectionId}/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> inpectionImage(@PathVariable Long dailyInspectionId, @PathVariable Long id) {
        return dailyInspectionService.imageForInspection(dailyInspectionId, id);
    }

    @PostMapping(path = "/pencapaian", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postPencapaian(@RequestBody Pencapaian pencapaian){
        try {
            return pencapaianService.addPencapaian(pencapaian);
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
            return pencapaianService.addPenyelesaian(id, penyelesaian, gambar);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/safetyTalk", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSafetyTalk(){
        try {
            return safetyTalkService.getAllSafetyTalk();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/safetyTalk/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailSafetyTalk(@PathVariable Long id){
        try {
            return safetyTalkService.getDetailSafetyTalk(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PostMapping(path = "/safetyTalk/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSafetyTalk(@RequestBody SafetyTalkDTO safetyTalk){
        try {
            return safetyTalkService.addSafetyTalk(safetyTalk);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/safetyTalk/export")
    public ResponseEntity<?> exportSafetyTalk(){
        return safetyTalkService.exportToExcel();
    }

    @GetMapping(path = "/statusStaff", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getStatusStaff(){
        try {
            return systemService.getAllStatusKaryawan();
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
    public ResponseEntity<?> searchStatusHistory(@RequestParam(value = "q", required = false) String search){
        try {
            return hazardReportService.searchAllHistoryStatus(search);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailHazardStatusHistory(@PathVariable Long id, HttpServletRequest request){
        try {
            return hazardReportService.getDetailHistoryStatus(id, request);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PutMapping(path = "/historyStatus/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> editHazardStatusHistory(@PathVariable Long id, @RequestBody HazardStatusDTO hazardStatusDTO){
        try {
            return hazardReportService.editHistoryStatus(id, hazardStatusDTO);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @DeleteMapping(path = "/historyStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> deleteHazardStatusHistory(@PathVariable Long id){
        try {
            return hazardReportService.deleteHistoryStatus(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(path = "/historyStatus/filter", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> filterSearchHistory(
            @RequestParam(value = "dept", required = false) String dept,
            @RequestParam(value = "status", required = false) String status){
        try {
            return hazardReportService.filterAllHistoryStatus(dept, status);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/historyStatus/export")
    public ResponseEntity<?> exportStatusHistory(){
        return hazardReportService.exportToExcel();
    }

    @GetMapping(value = "/users", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllUsers(){
        try {
            return systemService.getAllUser();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailUsers(@PathVariable Long id){
        try {
            return usersService.getDetailUser(id);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUsers(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        try {
            return usersService.updateProfile(id, updateUserDTO);
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/workArea", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllWorkArea() {
        try {
            return systemService.getAllWorkArea();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/workShift", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllWorkShift() {
        try {
            return systemService.getAllShift();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/findings", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllFindings() {
        try {
            return systemService.getAllFindings();
        } catch (Exception e){
            return handleException(e);
        }
    }

    @GetMapping(value = "/statusCompany", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllStatusCompany() {
        try {
            return systemService.getAllStatusCompany();
        } catch (Exception e) {
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
