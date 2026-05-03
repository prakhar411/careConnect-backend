package com.careconnect.controller;

import com.careconnect.dto.request.CredentialRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.CredentialResponse;
import com.careconnect.service.CredentialService;
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
@RequestMapping("/api/credentials")
@RequiredArgsConstructor
@Tag(name = "Credentials", description = "Nurse credentialing endpoints")
public class CredentialController {

    private final CredentialService credentialService;

    @PostMapping("/nurse/{nurseUserId}")
    @PreAuthorize("hasRole('NURSE')")
    @Operation(summary = "Add a credential for a nurse")
    public ResponseEntity<ApiResponse<CredentialResponse>> addCredential(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody CredentialRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Credential added", credentialService.addCredential(nurseUserId, request)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get credentials for a nurse")
    public ResponseEntity<ApiResponse<List<CredentialResponse>>> getByNurse(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(credentialService.getByNurse(nurseUserId)));
    }

    @GetMapping
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Get all credentials (admin view)")
    public ResponseEntity<ApiResponse<List<CredentialResponse>>> getAll() {
        return ResponseEntity.ok(ApiResponse.success(credentialService.getAll()));
    }

    @GetMapping("/org/{orgUserId}")
    @Operation(summary = "Get credentials of nurses who applied to this org")
    public ResponseEntity<ApiResponse<List<CredentialResponse>>> getByOrg(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(credentialService.getByOrg(orgUserId)));
    }

    @GetMapping("/expiring")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Get credentials expiring within N days")
    public ResponseEntity<ApiResponse<List<CredentialResponse>>> getExpiringSoon(
            @RequestParam(defaultValue = "30") int days) {
        return ResponseEntity.ok(ApiResponse.success(credentialService.getExpiringSoon(days)));
    }

    @PatchMapping("/{id}/verify")
    @PreAuthorize("hasRole('ORGANIZATION')")
    @Operation(summary = "Verify a credential")
    public ResponseEntity<ApiResponse<CredentialResponse>> verify(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Credential verified", credentialService.verify(id)));
    }
}
