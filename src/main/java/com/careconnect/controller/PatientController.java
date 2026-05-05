package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.entity.PatientProfile;
import com.careconnect.exception.UnauthorizedException;
import com.careconnect.repository.UserRepository;
import com.careconnect.service.PatientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/patients")
@RequiredArgsConstructor
@Tag(name = "Patients", description = "Patient profile endpoints")
public class PatientController {

    private final PatientService patientService;
    private final UserRepository userRepository;

    @GetMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Get the authenticated patient's profile")
    public ResponseEntity<ApiResponse<PatientProfile>> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getProfile(resolveUserId(userDetails))));
    }

    @GetMapping("/{userId}/profile")
    @Operation(summary = "Get a patient profile by user ID")
    public ResponseEntity<ApiResponse<PatientProfile>> getProfile(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success(patientService.getProfile(userId)));
    }

    @PutMapping("/{userId}/profile")
    @Operation(summary = "Update a patient profile by user ID")
    public ResponseEntity<ApiResponse<PatientProfile>> updateProfileById(
            @PathVariable Long userId,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                patientService.updateProfile(userId, updates)));
    }

    @PutMapping("/profile")
    @PreAuthorize("hasRole('PATIENT')")
    @Operation(summary = "Update the authenticated patient's profile")
    public ResponseEntity<ApiResponse<PatientProfile>> updateProfile(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestBody Map<String, Object> updates) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated",
                patientService.updateProfile(resolveUserId(userDetails), updates)));
    }

    private Long resolveUserId(UserDetails userDetails) {
        return userRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UnauthorizedException("Authenticated user not found"))
                .getId();
    }
}
