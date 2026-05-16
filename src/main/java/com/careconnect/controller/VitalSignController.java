package com.careconnect.controller;

import com.careconnect.dto.request.VitalSignRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.entity.VitalSign;
import com.careconnect.service.VitalSignService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vitals")
@RequiredArgsConstructor
@Tag(name = "Vital Signs", description = "Nurse vital sign logging endpoints")
public class VitalSignController {

    private final VitalSignService vitalSignService;

    @PostMapping
    @Operation(summary = "Log vital signs for a patient")
    public ResponseEntity<ApiResponse<VitalSign>> save(@RequestBody VitalSignRequest req) {
        VitalSign saved = vitalSignService.save(req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Vitals recorded", saved));
    }

    @GetMapping("/patient/{patientUserId}")
    @Operation(summary = "Get all vitals for a patient")
    public ResponseEntity<ApiResponse<List<VitalSign>>> getByPatient(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(vitalSignService.getByPatient(patientUserId)));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get vitals for a specific appointment")
    public ResponseEntity<ApiResponse<List<VitalSign>>> getByAppointment(@PathVariable Long appointmentId) {
        return ResponseEntity.ok(ApiResponse.success(vitalSignService.getByAppointment(appointmentId)));
    }
}
