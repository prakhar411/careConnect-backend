package com.careconnect.controller;

import com.careconnect.dto.request.ShiftRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.ShiftResponse;
import com.careconnect.service.ShiftService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shifts")
@RequiredArgsConstructor
@Tag(name = "Shifts", description = "Shift tracking between patient and nurse")
public class ShiftController {

    private final ShiftService shiftService;

    @PostMapping("/patient/{patientUserId}")
    @Operation(summary = "Patient marks a shift as done (pending nurse confirmation)")
    public ResponseEntity<ApiResponse<ShiftResponse>> markShift(
            @PathVariable Long patientUserId,
            @Valid @RequestBody ShiftRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Shift marked — awaiting nurse confirmation",
                        shiftService.markShift(patientUserId, request)));
    }

    @PatchMapping("/{shiftId}/confirm/{nurseUserId}")
    @Operation(summary = "Nurse confirms shift. acceptNegotiation=true uses patient's rate, false keeps original")
    public ResponseEntity<ApiResponse<ShiftResponse>> confirmShift(
            @PathVariable Long shiftId,
            @PathVariable Long nurseUserId,
            @RequestParam(defaultValue = "false") boolean acceptNegotiation) {
        return ResponseEntity.ok(ApiResponse.success("Shift confirmed",
                shiftService.confirmShift(nurseUserId, shiftId, acceptNegotiation)));
    }

    @PatchMapping("/{shiftId}/reject/{nurseUserId}")
    @Operation(summary = "Nurse rejects a shift")
    public ResponseEntity<ApiResponse<ShiftResponse>> rejectShift(
            @PathVariable Long shiftId,
            @PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Shift rejected",
                shiftService.rejectShift(nurseUserId, shiftId)));
    }

    @GetMapping("/appointment/{appointmentId}")
    @Operation(summary = "Get all shifts for a specific appointment")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getByAppointment(
            @PathVariable Long appointmentId) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.getByAppointment(appointmentId)));
    }

    @GetMapping("/patient/{patientUserId}")
    @Operation(summary = "Get all shifts marked by a patient (across all appointments)")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getByPatient(
            @PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.getByPatient(patientUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get all shifts for a nurse (across all patient appointments)")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getByNurse(
            @PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.getByNurse(nurseUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}/pending")
    @Operation(summary = "Get shifts pending nurse confirmation")
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getPendingByNurse(
            @PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(shiftService.getPendingByNurse(nurseUserId)));
    }
}
