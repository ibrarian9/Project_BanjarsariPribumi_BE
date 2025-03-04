package com.app.backendhazard.Controllers;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Models.Pencapaian;
import com.app.backendhazard.Service.*;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@AllArgsConstructor
@RestController
@RequestMapping("/api")
public class SystemControllers {

    private final UsersService usersService;
    private final CompanyService companyService;
    private final SafetyTalkService safetyTalkService;
    private final PencapaianService pencapaianService;
    private final DepartmentService departmentService;

    @GetMapping(path = "/company", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllCompany() {
        return companyService.getAllCompany();
    }

    @GetMapping(path = "/company/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailCompany(@PathVariable long id) {
        return companyService.getDetailCompany(id);
    }

    @PostMapping(path = "/company/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addCompany(@RequestBody CompanyDTO company) {
        return companyService.addCompany(company);
    }

    @GetMapping(path = "/departments", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllDepartment(){
        return departmentService.getAllDepartment();
    }

    @GetMapping(path = "/departments/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailDepartment(@PathVariable long id){
        return departmentService.getDetailDepartment(id);
    }

    @PostMapping(path = "/departments/add", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addDepartments(@RequestBody DepartmentsDTO department){
        return departmentService.addDepartment(department);
    }

    @GetMapping(path = "/resolutionImage/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> resolutionImage(@PathVariable Long id) {
        return pencapaianService.imageForResolution(id);
    }

    @PostMapping(path = "/pencapaian", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postPencapaian(@RequestBody Pencapaian pencapaian){
        return pencapaianService.addPencapaian(pencapaian);
    }

    @PostMapping(path = "/resolution/{id}/add", produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> postPenyelesaian(@PathVariable Long id
            ,@ModelAttribute("penyelesaian") PenyelesaianDTO penyelesaian
            ,@RequestPart(value = "gambar") MultipartFile gambar)
    {
        return pencapaianService.addPenyelesaian(id, penyelesaian, gambar);
    }

    @GetMapping(path = "/safetyTalk", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getSafetyTalk(){
        return safetyTalkService.getAllSafetyTalk();
    }

    @GetMapping(path = "/safetyTalk/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailSafetyTalk(@PathVariable Long id){
        return safetyTalkService.getDetailSafetyTalk(id);
    }

    @PostMapping(path = "/safetyTalk/add", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addSafetyTalk(@RequestBody SafetyTalkDTO safetyTalk){
        return safetyTalkService.addSafetyTalk(safetyTalk);
    }

    @GetMapping(value = "/safetyTalk/export")
    public ResponseEntity<?> exportSafetyTalk(){
        return safetyTalkService.exportToExcel();
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getDetailUsers(@PathVariable Long id){
        return usersService.getDetailUser(id);
    }

    @PutMapping(value = "/users/{id}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> updateUsers(@PathVariable Long id, @RequestBody UpdateUserDTO updateUserDTO) {
        return usersService.updateProfile(id, updateUserDTO);
    }

    @GetMapping(value = "/pencapaianSap/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getPencapaianSap(@PathVariable Long id){
        return usersService.pencapaianSAP(id);
    }

}
