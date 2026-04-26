package com.careconnect.controller;

import com.careconnect.dto.request.AppointmentRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.AppointmentResponse;
import com.careconnect.enums.AppointmentStatus;
import com.careconnect.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/appointments")
@RequiredArgsConstructor
@Tag(name = "Appointments", description = "Appointment booking and management endpoints")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping("/patient/{patientUserId}")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Book a new appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> bookAppointment(
            @PathVariable Long patientUserId,
            @Valid @RequestBody AppointmentRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Appointment booked",
                        appointmentService.bookAppointment(patientUserId, request)));
    }

    @GetMapping("/patient/{patientUserId}")
    @Operation(summary = "Get appointments for a patient")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByPatient(@PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getByPatient(patientUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get appointments assigned to a nurse")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getByNurse(nurseUserId)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update appointment status")
    public ResponseEntity<ApiResponse<AppointmentResponse>> updateStatus(
            @PathVariable Long id, @RequestParam AppointmentStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", appointmentService.updateStatus(id, status)));
    }
}
