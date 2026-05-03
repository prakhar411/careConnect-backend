package com.careconnect.controller;

import com.careconnect.dto.request.ApplicationRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.ApplicationResponse;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.service.ApplicationService;
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
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Nurse job application endpoints")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping("/nurse/{nurseUserId}")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Apply for a job")
    public ResponseEntity<ApiResponse<ApplicationResponse>> apply(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Application submitted", applicationService.apply(nurseUserId, request)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get all applications by a nurse")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getByNurse(nurseUserId)));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Get all applications for a job")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getByJob(@PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getByJob(jobId)));
    }

    @GetMapping("/org/{orgUserId}")
    @Operation(summary = "Get all applications for this organization's jobs")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getByOrg(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getByOrg(orgUserId)));
    }

    @GetMapping("/org/{orgUserId}/approved")
    @Operation(summary = "Get approved (hired) nurses for this organization")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getApprovedByOrg(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getApprovedByOrg(orgUserId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Get all applications (admin view)")
    public ResponseEntity<ApiResponse<List<ApplicationResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(applicationService.getAll()));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Approve or reject an application")
    public ResponseEntity<ApiResponse<ApplicationResponse>> updateStatus(
            @PathVariable Long id, @RequestParam ApplicationStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", applicationService.updateStatus(id, status)));
    }
}
