package com.careconnect.controller;

import com.careconnect.dto.request.UpdateOrgRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORGANIZATION')")
@Tag(name = "Admin", description = "Organization admin dashboard endpoints")
public class AdminController {

    private final OrganizationService organizationService;

    @GetMapping("/dashboard/{orgUserId}/stats")
    @Operation(summary = "Get org-specific statistics for admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getDashboardStats(orgUserId)));
    }

    @GetMapping("/org/{orgUserId}/profile")
    @Operation(summary = "Get organization profile details by user ID")
    public ResponseEntity<ApiResponse<Object>> getOrgProfile(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getProfile(orgUserId)));
    }

    @PatchMapping("/org/{orgUserId}/profile")
    @Operation(summary = "Update organization profile details")
    public ResponseEntity<ApiResponse<Object>> updateOrgProfile(
            @PathVariable Long orgUserId,
            @Valid @RequestBody UpdateOrgRequest request) {
        return ResponseEntity.ok(ApiResponse.success("Profile updated", organizationService.updateProfile(orgUserId, request)));
    }
}
