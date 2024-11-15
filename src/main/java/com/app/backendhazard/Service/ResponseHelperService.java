package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.DetailInspectionResponseDTO;
import com.app.backendhazard.Response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ResponseHelperService {

    public <T> ResponseEntity<Map<String, Object>> getAllData(List<T> list) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> getAllDataDTO(DetailInspectionResponseDTO list) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        response.put("data", list);
        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<Map<String, Object>> getDetailData(Long id, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.OK.value());
        Optional<T> optionalData = repository.findById(id);
        optionalData.ifPresent(value -> response.put("data", value));
        return ResponseEntity.ok(response);
    }

    public <T> ResponseEntity<Map<String, Object>> saveEntity(T entity, JpaRepository<T, Long> repository) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("data", repository.save(entity));
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, Object>> saveEntityWithMessage(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("httpStatus", HttpStatus.CREATED.value());
        response.put("message", message);
        return ResponseEntity.ok(response);
    }

    public String buildLinkImage(HttpServletRequest request, Long id, String apiUrl) {
        String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");
        return baseUrl + "/" + apiUrl + id;
    }

    public ResponseEntity<?> fetchImageReport(String imagePath, String notFoundMessage) {
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

    public ResponseEntity<?> handleException(Exception e) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }

    public ResponseEntity<?> handleExceptionByMessage(String s) {
        ErrorResponse errResponse = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(), s);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errResponse);
    }
}
