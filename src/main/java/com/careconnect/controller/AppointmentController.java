package com.careconnect.controller;

import com.careconnect.dto.request.AppointmentApplicationRequest;
import com.careconnect.dto.request.AppointmentRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.AppointmentApplicationResponse;
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

    @PatchMapping("/{id}/reschedule")
    @Operation(summary = "Reschedule an appointment to a new date/time")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reschedule(
            @PathVariable Long id, @RequestParam String newDate) {
        return ResponseEntity.ok(ApiResponse.success("Appointment rescheduled", appointmentService.reschedule(id, newDate)));
    }

    @GetMapping("/open")
    @Operation(summary = "Get all PENDING appointments with no nurse assigned (open patient requests)")
    public ResponseEntity<ApiResponse<List<AppointmentResponse>>> getOpenAppointments() {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getOpenAppointments()));
    }

    @PatchMapping("/{id}/assign")
    @Operation(summary = "Nurse accepts / gets assigned to a patient appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> assignNurse(
            @PathVariable Long id, @RequestParam Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Nurse assigned successfully", appointmentService.assignNurse(id, nurseUserId)));
    }

    @PostMapping("/{id}/apply")
    @Operation(summary = "Nurse applies to a patient appointment request with salary expectation")
    public ResponseEntity<ApiResponse<AppointmentApplicationResponse>> applyToAppointment(
            @PathVariable Long id,
            @RequestParam Long nurseUserId,
            @RequestBody AppointmentApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted", appointmentService.applyToAppointment(id, nurseUserId, request)));
    }

    @GetMapping("/{id}/applications")
    @Operation(summary = "Get all nurse applications (bids) for a patient appointment")
    public ResponseEntity<ApiResponse<List<AppointmentApplicationResponse>>> getAppointmentApplications(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getAppointmentApplications(id)));
    }

    @GetMapping("/applications/nurse/{nurseUserId}")
    @Operation(summary = "Get all appointment applications submitted by a nurse")
    public ResponseEntity<ApiResponse<List<AppointmentApplicationResponse>>> getNurseApplications(
            @PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(appointmentService.getNurseAppointmentApplications(nurseUserId)));
    }

    @PostMapping("/applications/{id}/accept")
    @Operation(summary = "Patient accepts a nurse application — assigns nurse and confirms appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> acceptApplication(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Nurse selected successfully", appointmentService.acceptApplication(id)));
    }

    @DeleteMapping("/{id}/apply")
    @Operation(summary = "Nurse withdraws their application from a patient appointment request")
    public ResponseEntity<ApiResponse<Void>> withdrawApplication(
            @PathVariable Long id, @RequestParam Long nurseUserId) {
        appointmentService.withdrawApplication(id, nurseUserId);
        return ResponseEntity.ok(ApiResponse.success("Application withdrawn", null));
    }

    @PostMapping("/{id}/reconcile/nurse")
    @Operation(summary = "Nurse confirms shift reconciliation for a completed appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reconcileByNurse(
            @PathVariable Long id, @RequestParam Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Reconciliation confirmed by nurse",
                appointmentService.reconcileByNurse(nurseUserId, id)));
    }

    @PostMapping("/{id}/reconcile/patient")
    @Operation(summary = "Patient confirms shift reconciliation for a completed appointment")
    public ResponseEntity<ApiResponse<AppointmentResponse>> reconcileByPatient(
            @PathVariable Long id, @RequestParam Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success("Reconciliation confirmed by patient",
                appointmentService.reconcileByPatient(patientUserId, id)));
    }
}
