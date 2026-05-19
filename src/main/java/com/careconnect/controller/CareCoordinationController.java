package com.careconnect.controller;

import com.careconnect.dto.request.CareTeamRequest;
import com.careconnect.dto.request.ProviderNoteRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.service.CareCoordinationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/care-coordination")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CareCoordinationController {

    private final CareCoordinationService service;

    // ── Care Team ─────────────────────────────────────────────────────────────

    @PostMapping("/patient/{patientUserId}/team")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addToTeam(
            @PathVariable Long patientUserId,
            @Valid @RequestBody CareTeamRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.addToTeam(patientUserId, request)));
    }

    @GetMapping("/patient/{patientUserId}/team")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getTeam(
            @PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(service.getTeam(patientUserId)));
    }

    @DeleteMapping("/team/{entryId}")
    public ResponseEntity<ApiResponse<Void>> removeFromTeam(@PathVariable Long entryId) {
        service.removeFromTeam(entryId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── Provider Notes ────────────────────────────────────────────────────────

    @PostMapping("/patient/{patientUserId}/notes")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addNote(
            @PathVariable Long patientUserId,
            @Valid @RequestBody ProviderNoteRequest request) {
        return ResponseEntity.ok(ApiResponse.success(service.addNote(patientUserId, request)));
    }

    @GetMapping("/patient/{patientUserId}/notes")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getNotes(
            @PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(service.getNotes(patientUserId)));
    }

    // ── Care Goals ────────────────────────────────────────────────────────────

    @PostMapping("/patient/{patientUserId}/goals")
    public ResponseEntity<ApiResponse<Map<String, Object>>> addGoal(
            @PathVariable Long patientUserId,
            @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ApiResponse.success(service.addGoal(patientUserId, request)));
    }

    @GetMapping("/patient/{patientUserId}/goals")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getGoals(
            @PathVariable Long patientUserId) {
        return ResponseEntity.ok(ApiResponse.success(service.getGoals(patientUserId)));
    }

    @PatchMapping("/goals/{goalId}/status")
    public ResponseEntity<ApiResponse<Map<String, Object>>> updateGoalStatus(
            @PathVariable Long goalId,
            @RequestParam String status) {
        return ResponseEntity.ok(ApiResponse.success(service.updateGoalStatus(goalId, status)));
    }

    @DeleteMapping("/goals/{goalId}")
    public ResponseEntity<ApiResponse<Void>> deleteGoal(@PathVariable Long goalId) {
        service.deleteGoal(goalId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // ── All team members for nurse dropdown ───────────────────────────────────

    @GetMapping("/team-members")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getAllTeamMembers() {
        return ResponseEntity.ok(ApiResponse.success(service.getAllTeamMembers()));
    }
}
