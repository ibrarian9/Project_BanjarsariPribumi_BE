package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.*;
import com.app.backendhazard.Handler.ExcelDateConverter;
import com.app.backendhazard.Handler.FolderImageApp;
import com.app.backendhazard.Models.*;
import com.app.backendhazard.Repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@AllArgsConstructor
public class DailyInspectionServiceImpl implements DailyInspectionService {

    private static final Logger log = LoggerFactory.getLogger(DailyInspectionServiceImpl.class);
    private final DailyInspectionRepository dailyInspectionRepo;
    private final DetailDailyInspectionRepository detailDailyInspectionRepo;
    private final StatusRepository statusRepo;
    private final QuestionInspectionRepository questionInspectionRepo;
    private final AnswerInspectionRepository answerInspectionRepo;
    private final DepartmentRepository departmentRepo;
    private final ShiftRepository shiftRepo;
    private final AreaKerjaRepository areaKerjaRepo;
    private final ResponseHelperService responseHelperService;
    private final FolderImageApp folderImageApp;
    private final UsersRepository usersRepository;
    private final InspectionAnswerRepository inspectionAnswerRepository;

    @Override
    public ResponseEntity<Map<String, Object>> getInspectionQuestion(Long areakerjaId) {
        return responseHelperService.getAllData(questionInspectionRepo.findByAreaKerjaId(areakerjaId));
    }

    @Transactional
    @Override
    public ResponseEntity<?> addInspectionAnswer(
            InspectionRequestDTO requestDTO,
            List<MultipartFile> gambarFiles
    ) {

        Status statusLaporan = statusRepo.findById(requestDTO.getDailyInspectionDTO().getStatusLaporanId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + requestDTO.getDailyInspectionDTO().getStatusLaporanId()));

        Department departmentPengawas = departmentRepo.findById(requestDTO.getDailyInspectionDTO().getDepartmentPengawasId())
                .orElseThrow(() -> new EntityNotFoundException("Department Pengawas Not Found " + requestDTO.getDailyInspectionDTO().getDepartmentPengawasId()));

        Shift shiftKerja = shiftRepo.findById(requestDTO.getDailyInspectionDTO().getShiftKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Shift Kerja Not Found " + requestDTO.getDailyInspectionDTO().getShiftKerjaId()));

        AreaKerja areaKerja = areaKerjaRepo.findById(requestDTO.getDailyInspectionDTO().getAreaKerjaId())
                .orElseThrow(() -> new EntityNotFoundException("Area Kerja Not Found " + requestDTO.getDailyInspectionDTO().getAreaKerjaId()));

        Users users = usersRepository.findById(requestDTO.getDailyInspectionDTO().getUserId())
                .orElseThrow(() -> new EntityNotFoundException("Users Not Found " + requestDTO.getDailyInspectionDTO().getUserId()));

        // Create new Daily Inspection Entity
        DailyInspection dailyInspection = new DailyInspection();
        dailyInspection.setNamaPengawas(requestDTO.getDailyInspectionDTO().getNamaPengawas());
        dailyInspection.setTanggalInspeksi(LocalDateTime.now().atZone(ZoneId.of("Asia/Jakarta")).toLocalDateTime());
        dailyInspection.setDepartmentPengawas(departmentPengawas);
        dailyInspection.setShiftKerja(shiftKerja);
        dailyInspection.setAreaKerja(areaKerja);
        dailyInspection.setStatus(statusLaporan);
        dailyInspection.setUser(users);
        dailyInspection.setKeteranganAreaKerja(requestDTO.getDailyInspectionDTO().getKeteranganAreaKerja());

        DailyInspection savedDailyInspection = dailyInspectionRepo.save(dailyInspection);

        List<DetailDailyInspection> detailToSave = new ArrayList<>();

        List<Integer> imageIndices = requestDTO.getImageIndicates() != null ?
                requestDTO.getImageIndicates() :
                new ArrayList<>();

        Map<Integer, MultipartFile> imageMap = new HashMap<>();
        for (int i = 0; i < gambarFiles.size() && i < imageIndices.size(); i++) {
            imageMap.put(imageIndices.get(i), gambarFiles.get(i));
        }

        for (int position = 0; position < requestDTO.getAnswerDTOList().size(); position++) {
            AnswerDTO answerDTO = requestDTO.getAnswerDTOList().get(position);

            // Check id
            InspectionQuestion inspectionQuestion = questionInspectionRepo.findById(answerDTO.getQuestionId())
                    .orElseThrow(() -> new EntityNotFoundException("Question Not Found " + answerDTO.getQuestionId()));

            Status status = statusRepo.findById(answerDTO.getStatusId())
                    .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + answerDTO.getStatusId()));

            // Create & save the Inspection Answer entity
            InspectionAnswer answer = new InspectionAnswer();
            answer.setJawaban(answerDTO.getJawaban());
            answer.setCatatan(answerDTO.getCatatan());
            answer.setStatus(status);

            // Check if image file
            MultipartFile imageFile = imageMap.get(position);
            if (imageFile != null) {
                try {
                    String filePath = saveImageToFileSystem(imageFile, savedDailyInspection.getId());
                    answer.setGambar(filePath);
                } catch (IOException e) {
                    return responseHelperService.handleException(e);
                }
            } else {
                answer.setGambar(null);
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

        return responseHelperService.saveEntityWithMessage("Answer added successfully");
    }

    @Override
    public ResponseEntity<Map<String, Object>> getDetailInspectionAnswer(Long id, HttpServletRequest request) {

        DailyInspection dailyInspection = dailyInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + id));

        List<DetailDailyInspection> detailList = detailDailyInspectionRepo.findByDailyInspectionId(id);

        List<QuestionAnswerDTO> questionAnswerDTOList = detailList.stream().map(detail -> {
            // Show Question
            QuestionAnswerDTO dto = new QuestionAnswerDTO();
            dto.setQuestionText(detail.getInspectionQuestion().getQuestion());

            Users lastUpdate = null;
            // Show Answer
            DetailAnswerDTO answerDTO = new DetailAnswerDTO();
            answerDTO.setId(detail.getInspectionAnswer().getId());
            answerDTO.setJawaban(detail.getInspectionAnswer().getJawaban());
            answerDTO.setCatatan(detail.getInspectionAnswer().getCatatan());
            answerDTO.setStatus(detail.getInspectionAnswer().getStatus());
            if (detail.getInspectionAnswer().getLastUpdate() != null) {
                lastUpdate = usersRepository.findById(detail.getInspectionAnswer().getLastUpdate().getId()).orElse(null);
            }
            if (lastUpdate != null) {
                answerDTO.setLastUpdate(lastUpdate.getUsername());
            }
            if (detail.getInspectionAnswer().getGambar() != null) {
                answerDTO.setImageLink(BuildLinkImage(request, id, detail.getInspectionAnswer().getId()));
            } else {
                answerDTO.setImageLink(null);
            }
            dto.setAnswerDetail(answerDTO);
            return dto;
        }).toList();

        // Set Answer Question to Daily Inspection
        dailyInspection.setDetailQuestionAnswers(questionAnswerDTOList);

        return responseHelperService.getAllDataDTO(dailyInspection);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllDailyInspection(String search, Integer size, Integer page) {
        Pageable pageable = PageRequest.of(page != null ? page : 0, size, Sort.by("id").descending());

        // Fetch All Detail Daily Inspection
        Page<DailyInspection> dailyInspectionsPage = detailDailyInspectionRepo.searchInspections(search, pageable);

        List<DailyInspection> dailyInspections = dailyInspectionsPage.getContent();

        return responseHelperService.getAllDataWithPage(dailyInspections, dailyInspections.size(), dailyInspectionsPage.getTotalPages());
    }

    @Transactional
    @Override
    public ResponseEntity<?> editStatusDailyInspection(Long id, DailyInspectionStatusDTO dailyInspectionStatusDTO) {
        // Find Daily Inspection By id
        DailyInspection dailyInspection = dailyInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + id));

        // Find Status by id
        Status status = statusRepo.findById(dailyInspectionStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + dailyInspectionStatusDTO.getStatusId()));

        // Update status & reason in daily inspection
        dailyInspection.setStatus(status);

        dailyInspection.setAlasan(responseHelperService.validateReason(status, dailyInspectionStatusDTO.getAlasan()));

        // save the updated Daily Inspection back to database
        dailyInspectionRepo.save(dailyInspection);

        return responseHelperService.saveEntityWithMessage("Daily Inspection Update Status Successfully");
    }

    @Transactional
    @Override
    public ResponseEntity<?> editStatusAnswer(Long id, UpdateInspectionStatusDTO inspectionStatusDTO) {
        InspectionAnswer inspectionAnswer = inspectionAnswerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspection Answer Not Found " + id));

        Status status = statusRepo.findById(inspectionStatusDTO.getStatusId())
                .orElseThrow(() -> new EntityNotFoundException("Status Not Found " + inspectionStatusDTO.getStatusId()));

        Users users = usersRepository.findById(inspectionStatusDTO.getLastUpdateId())
                .orElseThrow(() -> new EntityNotFoundException("User Not Found " + inspectionStatusDTO.getLastUpdateId()));

        inspectionAnswer.setStatus(status);
        inspectionAnswer.setLastUpdate(users);

        inspectionAnswerRepository.save(inspectionAnswer);
        return responseHelperService.saveEntityWithMessage("Daily Inspection Update Status Successfully");
    }

    @Override
    public ResponseEntity<?> deleteDailyInspection(Long id) {
        DailyInspection dailyInspection = dailyInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Daily Inspection Not Found " + id));

        List<DetailDailyInspection> details = detailDailyInspectionRepo.findByDailyInspectionId(id);

        List<Long> answerIds = details.stream()
                .map(detail -> detail.getInspectionAnswer().getId())
                .toList();

        if (!details.isEmpty()) {
            detailDailyInspectionRepo.deleteAll(details);
        }

        for (Long answerId : answerIds) {
            InspectionAnswer answer = answerInspectionRepo.findById(answerId).orElse(null);
            if (answer != null) {
                if (answer.getGambar() != null) {
                    deleteImageFile(answer.getGambar(), id);
                }
                answerInspectionRepo.delete(answer);
            }
        }

        dailyInspectionRepo.delete(dailyInspection);

        return responseHelperService.saveEntityWithMessage("Daily Inspection Delete Successfully");
    }

    @Transactional
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
        return responseHelperService.saveEntityWithMessage("Detail Daily Inspection added successfully");
    }

    @Override
    public ResponseEntity<?> imageForInspection(Long dailyInspectionId, Long id) {
        InspectionAnswer inspectionAnswer = answerInspectionRepo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Inspection Answer Not Found " + id));

        String imageUrl = folderImageApp.getFolderPath() + "ReportPic/dailyInspection/" + dailyInspectionId + "/" + inspectionAnswer.getGambar();

        return responseHelperService.fetchImageReport(imageUrl, "Daily Inspection Image Not Found");
    }

    @Override
    public ResponseEntity<?> exportToExcel() {
        List<DailyInspection> data = dailyInspectionRepo.findAll();

        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Daily Inspection");

            String[] headers = {
                    "No", "Tanggal", "Nama Pengawas", "Department",
                    "Shift Kerja", "Area Kerja", "Area Kerja Spesifik", "Status", "Alasan"
            };

            // Create header row dynamically
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }

            int rowNum = 1;
            for (DailyInspection inspection : data) {
                Row row = sheet.createRow(rowNum);

                Object[] values = {
                        rowNum,
                        inspection.getTanggalInspeksi() != null ?
                                ExcelDateConverter.formatDate(inspection.getTanggalInspeksi()) : "-",
                        inspection.getNamaPengawas(),
                        inspection.getDepartmentPengawas().getNamaDepartment(),
                        inspection.getShiftKerja().getNamaShift(),
                        inspection.getAreaKerja().getNamaAreaKerja(),
                        inspection.getKeteranganAreaKerja(),
                        inspection.getStatus().getNamaStatus(),
                        inspection.getAlasan() != null ? inspection.getAlasan() : "N/A"
                };

                for (int i = 0; i < values.length; i++) {
                    row.createCell(i).setCellValue(values[i].toString());
                }

                rowNum++;
            }

            // Generate Filename with Timestamp
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            String filename = "daily_inspection_" + timestamp + ".xlsx";

            return getResponseEntity(workbook, filename);
        } catch (IOException e) {
            return responseHelperService.handleException(e);
        }
    }

    @NotNull
    static ResponseEntity<?> getResponseEntity(Workbook workbook, String filename) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        HttpHeaders header = new HttpHeaders();
        header.add("Content-Disposition", "attachment; filename=" + filename);

        return ResponseEntity.ok()
                .headers(header)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(inputStream));
    }

    private String saveImageToFileSystem(MultipartFile imageFile, Long id) throws IOException {
        String uploadDir = folderImageApp.getFolderPath() + "ReportPic/dailyInspection/" + id + "/";
        Files.createDirectories(Paths.get(uploadDir)); // Ensure directory exists
        // Make file
        String fileName = UUID.randomUUID() + ".jpeg";
        File file = new File(uploadDir, fileName);
        // Write the file directly with FileCopyUtils
        try (InputStream in = imageFile.getInputStream(); OutputStream out = new FileOutputStream(file)) {
            FileCopyUtils.copy(in, out);
        }
        // Return the path to save in the database
        return fileName;
    }

    private void deleteImageFile(String gambar, Long id) {
        String imagePath = folderImageApp.getFolderPath() + "ReportPic/dailyInspection/" + id + "/" + gambar;
        File file = new File(imagePath);

        if (file.exists()) {
            if (file.delete()) {
                log.info("Image file deleted successfully: {}", imagePath);
            } else {
                log.error("Failed to delete image file: {}", imagePath);
            }
        }
    }

    private String BuildLinkImage(HttpServletRequest request, Long dailyInspectionId, Long id) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return baseUrl + "/api/imageDailyInspection/" + dailyInspectionId + "/" + id;
    }
}
