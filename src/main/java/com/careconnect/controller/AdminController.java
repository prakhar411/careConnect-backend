package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.OrganizationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @GetMapping("/dashboard/stats")
    @Operation(summary = "Get platform statistics for admin dashboard")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getDashboardStats() {
        return ResponseEntity.ok(ApiResponse.success(organizationService.getDashboardStats()));
    }
}
