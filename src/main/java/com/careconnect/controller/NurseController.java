package com.careconnect.controller;

import com.careconnect.dto.request.NurseBankDetailsRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.NurseResponse;
import com.careconnect.service.NurseService;
import com.careconnect.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/nurses")
@RequiredArgsConstructor
@Tag(name = "Nurses", description = "Nurse profile and search endpoints")
public class NurseController {

    private final NurseService nurseService;
    private final PaymentService paymentService;

    @GetMapping
    @Operation(summary = "Search all nurses or filter by specialization/availability")
    public ResponseEntity<ApiResponse<List<NurseResponse>>> searchNurses(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String availability) {
        return ResponseEntity.ok(ApiResponse.success(nurseService.searchNurses(specialization, availability)));
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get nurse profile by user ID")
    public ResponseEntity<ApiResponse<NurseResponse>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(nurseService.getProfile(userId)));
    }

    @PutMapping("/{userId}/profile")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Update nurse profile")
    public ResponseEntity<ApiResponse<NurseResponse>> updateProfile(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", nurseService.updateProfile(userId, updates)));
    }

    @PutMapping("/{userId}/bank-details")
    @Operation(summary = "Save nurse bank / UPI details for receiving payments")
    public ResponseEntity<ApiResponse<Void>> saveBankDetails(
            @PathVariable Long userId,
            @Valid @RequestBody NurseBankDetailsRequest request) {
        paymentService.saveBankDetails(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Bank details saved", null));
    }
}
