package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.entity.MedicalRecord;
import com.careconnect.service.MedicalRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/medical-records")
@RequiredArgsConstructor
@Tag(name = "Medical Records", description = "Patient medical record endpoints")
public class MedicalRecordController {

    private final MedicalRecordService medicalRecordService;

    @PostMapping("/patient/{patientUserId}/uploader/{uploaderUserId}")
    @Operation(summary = "Add a medical record for a patient")
    public ResponseEntity<ApiResponse<MedicalRecord>> addRecord(
            @PathVariable Long patientUserId,
            @PathVariable Long uploaderUserId,
            @RequestParam String recordType,
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String fileUrl) {
        MedicalRecord record = medicalRecordService.addRecord(
                patientUserId, uploaderUserId, recordType, title, description, fileUrl);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success("Record added", record));
    }

    @GetMapping("/patient/{patientUserId}")
    @Operation(summary = "Get all medical records for a patient")
    public ResponseEntity<ApiResponse<List<MedicalRecord>>> getByPatient(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(medicalRecordService.getByPatient(patientUserId)));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('PATIENT', 'NURSE', 'ORGANIZATION')")
    @Operation(summary = "Delete a medical record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        medicalRecordService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Record deleted", null));
    }
}
