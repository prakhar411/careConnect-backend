package com.careconnect.controller;

import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.PlatformAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/platform")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class PlatformAdminController {

    private final PlatformAdminService service;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(service.getStats()));
    }

    @GetMapping("/compliance")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllCompliance() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllCompliance()));
    }

    @GetMapping("/escalations")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllEscalations() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllEscalations()));
    }

    @GetMapping("/users")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllUsers() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllUsers()));
    }

    @GetMapping("/policies")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getPolicies() {
        return ResponseEntity.ok(ApiResponse.success(service.getPolicies()));
    }

    @PostMapping("/policies")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addPolicy(
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ApiResponse.success(service.addPolicy(request)));
    }

    @DeleteMapping("/policies/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePolicy(@PathVariable Long id) {
        service.deletePolicy(id);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    @PatchMapping("/users/{id}/toggle")
    public ResponseEntity<ApiResponse<Map<String, Object>>> toggleUser(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.toggleUserStatus(id)));
    }

    @GetMapping("/audit")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAuditTrail() {
        return ResponseEntity.ok(ApiResponse.success(service.getAuditTrail()));
    }

    @GetMapping("/nurses")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNurseProfiles() {
        return ResponseEntity.ok(ApiResponse.success(service.getNurseProfiles()));
    }

    @PatchMapping("/nurses/{id}/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyNurse(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.verifyNurseLicense(id)));
    }

    @GetMapping("/orgs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllOrgs() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllOrganizations()));
    }

    @PatchMapping("/orgs/{id}/verify")
    public ResponseEntity<ApiResponse<Map<String, Object>>> verifyOrg(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(service.verifyOrganization(id)));
    }
}
