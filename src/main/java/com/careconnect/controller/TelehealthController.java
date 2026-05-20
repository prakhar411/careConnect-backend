package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.TelehealthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/telehealth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TelehealthController {

    private final TelehealthService service;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upload(
            @RequestParam("file")          MultipartFile file,
            @RequestParam("nurseUserId")   Long nurseUserId,
            @RequestParam("nurseName")     String nurseName,
            @RequestParam("patientUserId") Long patientUserId,
            @RequestParam("patientName")   String patientName,
            @RequestParam("title")         String title,
            @RequestParam("category")      String category,
            @RequestParam(value = "description", defaultValue = "") String description) {
        return ResponseEntity.ok(ApiResponse.success(
                service.upload(file, nurseUserId, nurseName, patientUserId, patientName, title, category, description)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(service.getByNurse(nurseUserId)));
    }

    @GetMapping("/patient/{patientUserId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getByPatient(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(service.getByPatient(patientUserId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
