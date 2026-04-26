package com.careconnect.controller;

import com.careconnect.dto.request.ComplianceRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.ComplianceResponse;
import com.careconnect.enums.ComplianceStatus;
import com.careconnect.service.ComplianceService;
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
@RequestMapping("/api/compliance")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ORGANIZATION')")
@Tag(name = "Compliance", description = "Compliance tracking endpoints")
public class ComplianceController {

    private final ComplianceService complianceService;

    @PostMapping("/org/{orgUserId}")
    @Operation(summary = "Create a compliance record")
    public ResponseEntity<ApiResponse<ComplianceResponse>> create(
            @PathVariable Long orgUserId,
            @Valid @RequestBody ComplianceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Compliance record created", complianceService.create(orgUserId, request)));
    }

    @GetMapping("/org/{orgUserId}")
    @Operation(summary = "Get all compliance records for an organization")
    public ResponseEntity<ApiResponse<List<ComplianceResponse>>> getByOrg(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(complianceService.getByOrganization(orgUserId)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update compliance record status")
    public ResponseEntity<ApiResponse<ComplianceResponse>> updateStatus(
            @PathVariable Long id, @RequestParam ComplianceStatus status) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", complianceService.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a compliance record")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        complianceService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Record deleted", null));
    }
}
