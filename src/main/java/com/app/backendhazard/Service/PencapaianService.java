package com.app.backendhazard.Service;

import com.app.backendhazard.DTO.PenyelesaianDTO;
import com.app.backendhazard.Models.Pencapaian;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface PencapaianService {
    ResponseEntity<Map<String, Object>> addPencapaian(Pencapaian pencapaian);
    ResponseEntity<?> addPenyelesaian(Long id, PenyelesaianDTO penyelesaian, MultipartFile gambar);
    ResponseEntity<?> imageForResolution(Long id);
}
