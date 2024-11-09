package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Handler.ExcelDateConverter;
import com.app.backendhazard.Handler.FileUploadUtil;
import com.app.backendhazard.Models.*;
import com.app.backendhazard.Repository.*;
import com.app.backendhazard.Response.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StreamUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SystemServiceImpl implements SystemService {

    private static final Logger log = LoggerFactory.getLogger(SystemServiceImpl.class);
    private final UsersRepository usersRepo;
    private final ShiftRepository shiftRepo;
    private final StatusRepository statusRepo;
    private final StatusKaryawanRepository statusKaryawanRepo;
    private final HazardStatusHistoryRepository hazardStatusHistoryRepo;
    private final SafetyTalkRepo safetyTalkRepo;
    private final CompanyRepository companyRepo;
    private final CompanyRepository perusahaanRepo;
    private final AreaKerjaRepository areaKerjaRepo;
    private final DepartmentRepository departmentRepo;
    private final PencapaianRepository pencapaianRepo;
    private final PenyelesaianRepository penyelesaianRepo;
    private final HazardReportRepository hazardReportRepo;
    private final DailyInspectionRepository dailyInspectionRepo;
    private final StatusCompanyRepository statusCompanyRepository;
    private final DetailDailyInspectionRepository detailDailyInspectionRepo;
    private final QuestionInspectionRepository questionInspectionRepo;
    private final AnswerInspectionRepository answerInspectionRepo;
    private final KategoriTemuanRepository kategoriTemuanRepo;
    private final Integer reject = 3;

    private <T> ResponseEntity<Map<String, Object>> getAllData(List<T> list) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> getAllDataDTO(DetailInspectionResponseDTO list) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<Map<String, Object>> getDetailData(Long id, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        Optional<T> optionalData = repository.findById(id);
        optionalData.ifPresent(value -> response.put("data", value));
        return ResponseEntity.ok(response);
    }

    private <T> ResponseEntity<Map<String, Object>> saveEntity(T entity, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("data", repository.save(entity));
        return ResponseEntity.ok(response);
    }

    private ResponseEntity<Map<String, Object>> saveEntityWithMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllCompany() {
        return getAllData(perusahaanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailCompany(Long id) {
        return getDetailData(id, perusahaanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addCompany(CompanyDTO company) {
        StatusCompany statusCompany = statusCompanyRepository.findById(company.getStatusCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found"));

        Company newCompany = new Company();
        newCompany.setNamaCompany(company.getNamaCompany());
        newCompany.setStatusCompany(statusCompany);

        return saveEntity(newCompany, perusahaanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllDepartment() {
        return getAllData(departmentRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailDepartment(Long id) {
        return getDetailData(id, departmentRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addDepartment(DepartmentsDTO department) {
        Company company = companyRepo.findById(department.getCompanyId())
                .orElseThrow(() -> new EntityNotFoundException("Company Not Found"));

        Department newDepartment = new Department();
        newDepartment.setNamaDepartment(department.getNamaDepartment());
        newDepartment.setCompany(company);

        return saveEntity(newDepartment, departmentRepo);
    }

    @Override
    public ResponseEntity<?> addHazardReport(HazardReportDTO hazardReport, MultipartFile gambar) {

        // Create & set new Report
        HazardReport newReport = new HazardReport();
        newReport.setTitle(hazardReport.getTitle());
        newReport.setNamaPelapor(hazardReport.getNamaPelapor());
        newReport.setLokasi(hazardReport.getLokasi());
        newReport.setDeskripsi(hazardReport.getDeskripsi());
        newReport.setTindakan(hazardReport.getTindakan());

        // Set Related id with error handling
        KategoriTemuan kategoriTemuan = kategoriTemuanRepo.findById(hazardReport.getKategoriTemuanId())
                .orElseThrow(() -> new EntityNotFoundException("Kategori Temuan Not Found " + hazardReport.getKategoriTemuanId()));

        Department departmentPelapor = departmentRepo.findById(hazardReport.getDepartmentPelaporId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pelapor Not Found " + hazardReport.getDepartmentPelaporId()));

        Department departmentPerbaikan = departmentRepo.findById(hazardReport.getDepartmentPerbaikanId())
                .orElseThrow(() -> new EntityNotFoundException("Department Perbaikan Not Found " + hazardReport.getDepartmentPerbaikanId()));

        newReport.setKategoriTemuan(kategoriTemuan);
        newReport.setDepartmentPelapor(departmentPelapor);
        newReport.setDepartmentPerbaikan(departmentPerbaikan);
        newReport.setTanggalKejadian(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        // Save initial report without the image
        HazardReport savedReport = hazardReportRepo.save(newReport);

        // Handle Image Upload
        String nameGambar = "gambar_" + System.currentTimeMillis() + ".jpg";
        savedReport.setGambar(nameGambar);

        HazardReport hazardReportWithId = hazardReportRepo.save(savedReport);
        String uploadDir = "upload/hazardReport/" + hazardReportWithId.getId();

        // Save the image file
        try {
            FileUploadUtil.saveFile(uploadDir, nameGambar, gambar);
        } catch (Exception e){
            return handleException(e);
        }

        // Add Status To History
        HazardStatusHistory history = new HazardStatusHistory();
        Status status = statusRepo.findById(1L)
                .orElseThrow(() -> new EntityNotFoundException("Status Pelapor Not Found" + 1L));
        history.setReport(hazardReportWithId);
        history.setStatus(status);
        history.setUpdateBy("Admin");
        history.setUpdateDate(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        hazardStatusHistoryRepo.save(history);
        return saveEntityWithMessage("Hazard Report Berhasil Dibuat!");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllInspection() {
        return getAllData(dailyInspectionRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getInspectionQuestion(Long areakerjaId) {
        return getAllData(questionInspectionRepo.findByAreaKerjaId(areakerjaId));
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailInspection(Long id) {
        return getDetailData(id, dailyInspectionRepo);
    }

    @Transactional
    @Override
    public ResponseEntity<?> addInspectionAnswer(
           InspectionRequestDTO requestDTO,
           List<MultipartFile> gambarFiles
    ) {
        // Set Status to Open
        Long open = 1L;

        log.info("data request DTO: {}", requestDTO);

        // Check id
        Status status = statusRepo.findById(open)
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + open));

        Department departmentPengawas = departmentRepo.findById(requestDTO.getDailyInspectionDTO().getDepartmentPengawasId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pengawas Not Found " + requestDTO.getDailyInspectionDTO().getDepartmentPengawasId()));

        Shift shiftKerja = shiftRepo.findById(requestDTO.getDailyInspectionDTO().getShiftKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Shift Kerja Not Found " + requestDTO.getDailyInspectionDTO().getShiftKerjaId()));

        AreaKerja areaKerja = areaKerjaRepo.findById(requestDTO.getDailyInspectionDTO().getAreaKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Area Kerja Not Found " + requestDTO.getDailyInspectionDTO().getAreaKerjaId()));

        // Create new Daily Inspection Entity
        DailyInspection dailyInspection = new DailyInspection();
        dailyInspection.setNamaPengawas(requestDTO.getDailyInspectionDTO().getNamaPengawas());
        dailyInspection.setTanggalInspeksi(requestDTO.getDailyInspectionDTO().getTanggalInspeksi());
        dailyInspection.setDepartmentPengawas(departmentPengawas);
        dailyInspection.setShiftKerja(shiftKerja);
        dailyInspection.setAreaKerja(areaKerja);
        dailyInspection.setStatus(status);
        dailyInspection.setKeteranganAreaKerja(requestDTO.getDailyInspectionDTO().getKeteranganAreaKerja());

        DailyInspection savedDailyInspection = dailyInspectionRepo.save(dailyInspection);

        List<DetailDailyInspection> detailToSave = new ArrayList<>();

        // to map image files to each answerDTO
        int imageIndex = 0;
        // Process each AnswerDTO & Create Detail Daily Inspection records
        for (AnswerDTO answerDTO : requestDTO.getAnswerDTOList() ) {
            // Check id
            InspectionQuestion inspectionQuestion = questionInspectionRepo.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question Not Found " + answerDTO.getQuestionId()));

            // Create & save the Inspection Answer entity
            InspectionAnswer answer = new InspectionAnswer();
            answer.setJawaban(answerDTO.getJawaban());
            answer.setCatatan(answerDTO.getCatatan());

            // Check if image file
            if (!gambarFiles.isEmpty() && imageIndex < gambarFiles.size()) {
                MultipartFile imageFile = gambarFiles.get(imageIndex++);
                try {
                    String filePath = saveImageToFileSystem(imageFile, savedDailyInspection.getId());
                    answer.setGambar(filePath);
                } catch (IOException e) {
                    return handleException(e);
                }
            }

            // save answer & directly use the saved instance
            InspectionAnswer savedAnswer = answerInspectionRepo.save(answer);

            // Create a new Detail Daily Inspection entry
            DetailDailyInspection detailDailyInspection = new DetailDailyInspection();
            detailDailyInspection.setInspectionQuestion(inspectionQuestion);
            detailDailyInspection.setInspectionAnswer(savedAnswer);
            detailDailyInspection.setDailyInspection(savedDailyInspection);

            // Add to List for batch saving
            detailToSave.add(detailDailyInspection);
        }
        // Batch save all DetailDailyInspection record
        detailDailyInspectionRepo.saveAll(detailToSave);

        return saveEntityWithMessage("Answer added successfully");
    }

    private String saveImageToFileSystem(MultipartFile imageFile, Long id) throws IOException {
        String uploadDir = "upload/dailyInspection/" + id + "/";
        Files.createDirectories(Paths.get(uploadDir)); // Ensure directory exists
        // Make file
        String fileName = UUID.randomUUID() + ".jpg";
        File file = new File(uploadDir, fileName);
        // Write the file directly with FileCopyUtils
        try (InputStream in = imageFile.getInputStream(); OutputStream out = new FileOutputStream(file)) {
            FileCopyUtils.copy(in, out);
        }
        // Return the path to save in the database
        return fileName;
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailInspectionAnswer(Long id) {

        DailyInspection dailyInspection = dailyInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + id));

        List<DetailDailyInspection> detailList = detailDailyInspectionRepo.findByDailyInspectionId(id);

        List<QuestionAnswerDTO> questionAnswerDTOList = detailList.stream().map(detail -> {
            // Show Question
            QuestionAnswerDTO dto = new QuestionAnswerDTO();
            dto.setQuestionText(detail.getInspectionQuestion().getQuestion());

            // Show Answer
            DetailAnswerDTO answerDTO = new DetailAnswerDTO();
            answerDTO.setJawaban(detail.getInspectionAnswer().getJawaban());
            answerDTO.setCatatan(detail.getInspectionAnswer().getCatatan());

            dto.setAnswerDetail(answerDTO);
            return dto;
        }).toList();

        // Set Answer Question to Daily Inspection
        dailyInspection.setDetailQuestionAnswers(questionAnswerDTOList);

        // Wrap the modified Daily Inspection in the response DTO
        DetailInspectionResponseDTO responseDTO = new DetailInspectionResponseDTO();
        responseDTO.setDailyInspection(dailyInspection);

        return getAllDataDTO(responseDTO);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllDailyInspection(String search) {
        // Fetch All Detail Daily Inspection
        List<DetailDailyInspection> dailyInspections = (search == null || search.isEmpty())
                ? detailDailyInspectionRepo.findAll() : detailDailyInspectionRepo.searchInspections(search);

        // Group by DailyInspection ID
        Map<Long, List<DetailDailyInspection>> groupByDailyInspection = dailyInspections.stream()
                .collect(Collectors.groupingBy(d -> d.getDailyInspection().getId()));

        // Map each Detail Daily Inspection to a structured response with its related question
        List<DetailInspectionResponseDTO> inspectionResponseDTO = groupByDailyInspection.values().stream().map(detailData -> {

            DailyInspection dailyInspection = detailData.get(0).getDailyInspection();

            // Map each Detail Daily Inspection to a DTO
            List<QuestionAnswerDTO> questionAnswerDTOList = detailData.stream().map(data -> {
                // Set Answer
                DetailAnswerDTO answerDTO = new DetailAnswerDTO();
                answerDTO.setCatatan(data.getInspectionAnswer().getCatatan());
                answerDTO.setJawaban(data.getInspectionAnswer().getJawaban());

                // Set Question Answer
                QuestionAnswerDTO dto = new QuestionAnswerDTO();
                dto.setQuestionText(data.getInspectionQuestion().getQuestion());
                dto.setAnswerDetail(answerDTO);

                return dto;
            }).toList();

            dailyInspection.setDetailQuestionAnswers(questionAnswerDTOList);

            // Set Daily Inspection & Question Answer
            DetailInspectionResponseDTO responseDTO = new DetailInspectionResponseDTO();
            responseDTO.setDailyInspection(dailyInspection);

            return responseDTO;
        }).toList();

        return getAllData(inspectionResponseDTO);
    }

    @Override
    public ResponseEntity<?> editStatusDailyInspection(Long id, DailyInspectionStatusDTO dailyInspectionStatusDTO) {
        // Find Daily Inspection By id
        DailyInspection dailyInspection = dailyInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + id));

        // Find Status by id
        Status status = statusRepo.findById(dailyInspectionStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found "+ dailyInspectionStatusDTO.getStatusId()));

        // Update status & reason in daily inspection
        dailyInspection.setStatus(status);

        // Cek Status if Reject make the Reason required
        if (status.getId() != null && status.getId().equals(reject)) {
            if (!StringUtils.hasText(dailyInspectionStatusDTO.getAlasan())) {
                ErrorResponse errorRes = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Reason is required when status is Rejected");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
            }
            dailyInspection.setAlasan(dailyInspectionStatusDTO.getAlasan());
        } else {
            dailyInspection.setAlasan(dailyInspectionStatusDTO.getAlasan());
        }

        // save the updated Daily Inspection back to database
        dailyInspectionRepo.save(dailyInspection);

        return saveEntityWithMessage("Daily Inspection Update Status Successfully");
    }

    @Override
    public ResponseEntity<Map<String, Object>> addDetailDailyInspection(DetailInspectionDTO detailInspectionDTO) {
        DailyInspection dailyInspection = dailyInspectionRepo.findById(detailInspectionDTO.getDailyInspectionId())
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + detailInspectionDTO.getDailyInspectionId()));

        InspectionQuestion inspectionQuestion = questionInspectionRepo.findById(detailInspectionDTO.getQuestionId())
                .orElseThrow(() -> new EntityNotFoundException("Question Not Found " + detailInspectionDTO.getQuestionId()));

        InspectionAnswer inspectionAnswer = answerInspectionRepo.findById(detailInspectionDTO.getAnswerId())
                .orElseThrow(() -> new EntityNotFoundException("Answer Not Found " + detailInspectionDTO.getAnswerId()));

        DetailDailyInspection detailDailyInspection = new DetailDailyInspection();
        detailDailyInspection.setDailyInspection(dailyInspection);
        detailDailyInspection.setInspectionQuestion(inspectionQuestion);
        detailDailyInspection.setInspectionAnswer(inspectionAnswer);

        detailDailyInspectionRepo.save(detailDailyInspection);
        return saveEntityWithMessage("Detail Daily Inspection added successfully");
    }

    @Override
    public ResponseEntity<Map<String, Object>> addPencapaian(Pencapaian pencapaian) {
        return saveEntity(pencapaian, pencapaianRepo);
    }

    @Override
    public ResponseEntity<?> addPenyelesaian(Long id, PenyelesaianDTO penyelesaian, MultipartFile gambar) {
        // check id department & hazard report exist
        Department department = departmentRepo.findById(penyelesaian.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found"));
        HazardReport hazardReport = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found"))
                .getReport();

        // Create or retrieve existing Penyelesaian
        Penyelesaian penyelesaianEntity = hazardReport.getPenyelesaian();
        if (penyelesaianEntity == null) {
            penyelesaianEntity = new Penyelesaian();
        }

        // Set Penyelesaian
        penyelesaianEntity.setDepartment(department);
        penyelesaianEntity.setNamaPenyelesaian(penyelesaian.getNamaPenyelesaian());

        // Save Penyelesaian to assign an ID before handling the image
        penyelesaianEntity = penyelesaianRepo.save(penyelesaianEntity);

        // Check the ir have image or not
        if (gambar != null && !gambar.isEmpty()) {
            String oldImage = penyelesaianEntity.getGambar();

            // make name for image
            String namaGambar = "resolution_" + System.currentTimeMillis() + ".jpg";
            penyelesaianEntity.setGambar(namaGambar);

            String uploadDir = "upload/resolution/" + penyelesaianEntity.getId();
            try {
                if (oldImage != null && !oldImage.isEmpty()) {
                    File oldFile = new File(uploadDir, oldImage);
                    if (oldFile.exists() && !oldFile.delete()) {
                        return handleException(new IOException("Failed to delete old file: " + oldImage));
                    }
                }
                // Save the new image
                FileUploadUtil.saveFile(uploadDir, namaGambar, gambar);

                // Save the update penyelesaian Entity with the new Image name
                penyelesaianEntity = penyelesaianRepo.save(penyelesaianEntity);
            } catch (Exception e) {
                return handleException(e);
            }
        }

        // Update Hazard Report with the new Penyelesaian if not set
        if (hazardReport.getPenyelesaian() == null) {
            hazardReport.setPenyelesaian(penyelesaianEntity);
            hazardReportRepo.save(hazardReport);
        }

        // Prepare Response
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", penyelesaianEntity.getId() == null ? "Resolution Added Succesfully" : "Resolution Updated Succesfully");
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<?> imageForHazardReport(Long id) {
        HazardReport hazardReport = hazardStatusHistoryRepo.findByReportId(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id)).getReport();

        String imageUrl = "upload/hazardReport/" + hazardReport.getId() + "/" + hazardReport.getGambar();

        return fetchImageReport(imageUrl, "Hazard Report Image Not Found");
    }

    @Override
    public ResponseEntity<?> imageForResolution(Long id) {
        Penyelesaian penyelesaian = penyelesaianRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Penyelesaian Not Found " + id));

        log.info("tampil penyelesaian {}", penyelesaian);

        String imageUrl = "upload/resolution/" + penyelesaian.getId() + "/" + penyelesaian.getGambar();

        return fetchImageReport(imageUrl, "Resolution Image Not Found");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllSafetyTalk() {
        return getAllData(safetyTalkRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailSafetyTalk(Long id) {
        return getDetailData(id, safetyTalkRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> addSafetyTalk(SafetyTalkDTO safetyTalk) {

        log.info("safety talk: {}", safetyTalk);

        Users users = usersRepo.findById(safetyTalk.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("User Not Found " + safetyTalk.getUserId()));

        Department department = departmentRepo.findById(safetyTalk.getDepartmentId())
                .orElseThrow(() -> new EntityNotFoundException("Department Not Found " + safetyTalk.getDepartmentId()));

        SafetyTalk newSafetyTalk = new SafetyTalk();
        newSafetyTalk.setDepartment(department);
        newSafetyTalk.setUser(users);
        newSafetyTalk.setAttaintmentNumber(safetyTalk.getAttainmentNum());
        newSafetyTalk.setTanggal(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        safetyTalkRepo.save(newSafetyTalk);

        return saveEntityWithMessage("Safety Talk Berhasil Ditambahkan");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatusKaryawan() {
        return getAllData(statusKaryawanRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailStatusKaryawan(Long id) {
        return getDetailData(id, statusKaryawanRepo);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllStatus() {
        return getAllData(statusRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailHistoryStatus(Long id, HttpServletRequest request) {
        // Check id
        HazardStatusHistory hazardReport = hazardStatusHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id));

        // Penyelesaian DTO
        ResponsePenyelesaianDTO penyelesaianDTO = null;
        Penyelesaian penyelesaian = hazardReport.getReport().getPenyelesaian();
        if (penyelesaian != null){
            penyelesaianDTO = new ResponsePenyelesaianDTO();
            penyelesaianDTO.setNamaPenyelesaian(hazardReport.getReport().getPenyelesaian().getNamaPenyelesaian());
            penyelesaianDTO.setDepartment(hazardReport.getReport().getPenyelesaian().getDepartment());
            penyelesaianDTO.setLinkGambar(buildLinkImage(request, hazardReport.getReport().getPenyelesaian().getId(), "api/resolutionImage/"));
        }

        // Map Hazard Report fields to DTO
        ResponseHazardReportDTO resHazardReportDTO = new ResponseHazardReportDTO();
        resHazardReportDTO.setId(hazardReport.getReport().getId());
        resHazardReportDTO.setTitle(hazardReport.getReport().getTitle());
        resHazardReportDTO.setNamaPelapor(hazardReport.getReport().getNamaPelapor());
        resHazardReportDTO.setLokasi(hazardReport.getReport().getLokasi());
        resHazardReportDTO.setDeskripsi(hazardReport.getReport().getDeskripsi());
        resHazardReportDTO.setKategoriTemuan(hazardReport.getReport().getKategoriTemuan());
        resHazardReportDTO.setDepartmentPelapor(hazardReport.getReport().getDepartmentPelapor());
        resHazardReportDTO.setDepartmentPerbaikan(hazardReport.getReport().getDepartmentPerbaikan());
        resHazardReportDTO.setPenyelesaian(penyelesaianDTO);
        resHazardReportDTO.setTindakan(hazardReport.getReport().getTindakan());
        resHazardReportDTO.setTanggalKejadian(hazardReport.getReport().getTanggalKejadian());
        resHazardReportDTO.setLinkGambar(buildLinkImage(request, hazardReport.getReport().getId(), "api/gambar/"));

        // HIstory Status
        HistoryStatusDTO historyStatusDTO = new HistoryStatusDTO();
        historyStatusDTO.setId(hazardReport.getId());
        historyStatusDTO.setReport(resHazardReportDTO);
        historyStatusDTO.setStatus(hazardReport.getStatus());
        historyStatusDTO.setAlasan(hazardReport.getAlasan());
        historyStatusDTO.setUpdateBy(hazardReport.getUpdateBy());
        historyStatusDTO.setUpdateDate(hazardReport.getUpdateDate());

        // Create the response map
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", historyStatusDTO);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> searchAllHistoryStatus(String search) {
        List<HazardStatusHistory> data = hazardStatusHistoryRepo.searchHazardStatusHistory(search);
        data.sort(Comparator.comparing(HazardStatusHistory::getId).reversed());
        return getAllData(data);
    }

    @Override
    public ResponseEntity<Map<String, Object>> filterAllHistoryStatus(String dept, String status) {
        return getAllData(hazardStatusHistoryRepo.filterByDepartmentAndStatus(dept, status));
    }

    @Override
    public ResponseEntity<?> editHistoryStatus(Long id, HazardStatusDTO hazardStatusDTO) {
        // Cek id hazard report
        HazardReport hazardReport = hazardReportRepo.findById(hazardStatusDTO.getReportId())
                .orElseThrow(() -> new EntityNotFoundException("Report Not Found " + hazardStatusDTO.getReportId()));
        // Cek id Status
        Status status = statusRepo.findById(hazardStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + hazardStatusDTO.getStatusId()));
        // Cek id History Hazard
        HazardStatusHistory history = hazardStatusHistoryRepo.findByReportId(hazardStatusDTO.getReportId())
                .orElse(new HazardStatusHistory());

        history.setReport(hazardReport);
        history.setStatus(status);

        // Cek when status reject, alasan required
        if (status.getId() != null && status.getId().equals(reject)) {
            if (!StringUtils.hasText(hazardStatusDTO.getAlasan())) {
                ErrorResponse errorRes = new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "Alasan is required when status is Rejected");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorRes);
            }
            history.setAlasan(hazardStatusDTO.getAlasan());
        } else {
            history.setAlasan(hazardStatusDTO.getAlasan());
        }

        history.setUpdateBy(hazardStatusDTO.getUpdateBy());
        history.setUpdateDate(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());

        // Update Status Hazard Report
        hazardStatusHistoryRepo.save(history);

        return saveEntityWithMessage("Status Hazard Report updated successfully!");
    }

    @Override
    public ResponseEntity<?> deleteHistoryStatus(Long id) {
        // fetch hazard report status id
        HazardStatusHistory hazardStatusHistory = hazardStatusHistoryRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Hazard Report Not Found " + id));

        HazardReport hazardReport = hazardStatusHistory.getReport();

        // Define tje file paths
        String fileName = hazardReport.getGambar();
        String filePath =  "upload/hazardReport/" + hazardReport.getId();
        File fileToDelete = new File(filePath, fileName);

        // Delete the specific file if it exists
        if (fileToDelete.exists() && !fileToDelete.delete()) {
            return handleExceptionByMessage("Failed to delete the file: " + fileToDelete.getPath());
        }

        // Delete the entire directory if it exists
        File directory = new File(filePath);
        if (directory.exists() && directory.isDirectory()) {
            try {
                FileUtils.deleteDirectory(directory);
            } catch (IOException e) {
                return handleException(e);
            }
        }
        // Delete hazard status history with this hazard report
        hazardStatusHistoryRepo.delete(hazardStatusHistory);

        // Delete the hazard report from the repository
        hazardReportRepo.delete(hazardReport);

        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("message", "Delete Berhasil");

        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllUser() {
        return getAllData(usersRepo.findByRoleId(2L));
    }

    @Override
    public ResponseEntity<?> exportToExcel() {
        List<HazardStatusHistory> data = hazardStatusHistoryRepo.findAll();

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Hazard Report");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("No");
            headerRow.createCell(1).setCellValue("Tanggal");
            headerRow.createCell(2).setCellValue("Judul");
            headerRow.createCell(3).setCellValue("Nama");
            headerRow.createCell(4).setCellValue("Department");
            headerRow.createCell(5).setCellValue("Perusahaan");
            headerRow.createCell(6).setCellValue("Deskripsi");
            headerRow.createCell(7).setCellValue("Lokasi");
            headerRow.createCell(8).setCellValue("Jenis Temuan");
            headerRow.createCell(9).setCellValue("Tindakan Perbaikan");
            headerRow.createCell(10).setCellValue("Department Perbaikan");
            headerRow.createCell(11).setCellValue("Perusahaan Pic");
            headerRow.createCell(12).setCellValue("Status");

            int rowNum = 1;
            for (HazardStatusHistory report : data) {
                Row row = sheet.createRow(rowNum);
                row.createCell(0).setCellValue(rowNum);
                if (report.getReport().getTanggalKejadian() != null){
                    row.createCell(1).setCellValue(ExcelDateConverter.formatDate(report.getReport().getTanggalKejadian()));
                }
                row.createCell(2).setCellValue(report.getReport().getTitle());
                row.createCell(3).setCellValue(report.getReport().getNamaPelapor());
                row.createCell(4).setCellValue(report.getReport().getDepartmentPelapor().getNamaDepartment());
                row.createCell(5).setCellValue(report.getReport().getDepartmentPelapor().getCompany().getNamaCompany());
                row.createCell(6).setCellValue(report.getReport().getDeskripsi());
                row.createCell(7).setCellValue(report.getReport().getLokasi());
                row.createCell(8).setCellValue(report.getReport().getKategoriTemuan().getKategoriTemuan());
                row.createCell(9).setCellValue(report.getReport().getTindakan());
                row.createCell(10).setCellValue(report.getReport().getDepartmentPerbaikan().getNamaDepartment());
                row.createCell(11).setCellValue(report.getReport().getDepartmentPerbaikan().getCompany().getNamaCompany());
                row.createCell(12).setCellValue(report.getStatus().getNamaStatus());

                rowNum++;
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

            HttpHeaders headers = new HttpHeaders();
            headers.add("Content-Disposition", "attachment; filename=hazard_reports.xlsx");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                    .body(new InputStreamResource(inputStream));
        } catch (IOException e){
            return handleException(e);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllWorkArea() {
        return getAllData(areaKerjaRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllShift() {
        return getAllData(shiftRepo.findAll());
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllFindings() {
        return getAllData(kategoriTemuanRepo.findAll());
    }

    private ResponseEntity<?> fetchImageReport(String imagePath, String notFoundMessage) {
        try {
            // Check if the image path is null
            if (imagePath == null) {
                return handleExceptionByMessage(notFoundMessage);
            }

            // Use FileSystemResource to access the image
            FileSystemResource imageRes = new FileSystemResource(imagePath);

            // Verify if the file exists
            if (!imageRes.exists()) {
                return handleExceptionByMessage(notFoundMessage);
            }

            // Read image bytes and return as response
            byte[] bytes = StreamUtils.copyToByteArray(imageRes.getInputStream());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + imagePath + "\"")
                    .body(bytes);
        } catch (Exception e) {
            return handleException(e);
        }
    }

    private String buildLinkImage(HttpServletRequest request, Long id, String apiUrl) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return baseUrl + "/" + apiUrl + id;
    }

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

    public ResponseEntity<?> handleExceptionByMessage(String s) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), s);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

}
