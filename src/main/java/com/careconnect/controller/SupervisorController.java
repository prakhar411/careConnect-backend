package com.careconnect.controller;

import com.careconnect.dto.request.EscalationRequest;
import com.careconnect.dto.request.HandoffNoteRequest;
import com.careconnect.dto.response.*;
import com.careconnect.service.SupervisorService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/supervisor")
@RequiredArgsConstructor
@Tag(name = "Supervisor", description = "Org supervisor — team workload and job assignment management")
public class SupervisorController {

    private final SupervisorService supervisorService;

    @GetMapping("/{orgUserId}/workload")
    @Operation(summary = "Get workload summary for all hired nurses in org")
    public ResponseEntity<ApiResponse<List<NurseWorkloadResponse>>> getWorkload(
            @PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(supervisorService.getWorkload(orgUserId)));
    }

    @GetMapping("/{orgUserId}/jobs")
    @Operation(summary = "Get all org jobs with their assigned nurses")
    public ResponseEntity<ApiResponse<List<JobAssignmentResponse>>> getJobAssignments(
            @PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(supervisorService.getJobAssignments(orgUserId)));
    }

    @PostMapping("/{orgUserId}/assign")
    @Operation(summary = "Supervisor directly assigns a nurse to an unassigned job")
    public ResponseEntity<ApiResponse<JobAssignmentResponse>> assignNurse(
            @PathVariable Long orgUserId,
            @RequestParam Long jobId,
            @RequestParam Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Nurse assigned successfully",
                supervisorService.assignNurse(orgUserId, jobId, nurseUserId)));
    }

    @PatchMapping("/{orgUserId}/reassign")
    @Operation(summary = "Supervisor reassigns a nurse to a different nurse on a job")
    public ResponseEntity<ApiResponse<JobAssignmentResponse>> reassignNurse(
            @PathVariable Long orgUserId,
            @RequestParam Long jobId,
            @RequestParam Long newNurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Nurse reassigned successfully",
                supervisorService.reassignNurse(orgUserId, jobId, newNurseUserId)));
    }

    // ── Shift Coverage (AC 16.3) ──────────────────────────────────────────────

    @GetMapping("/{orgUserId}/shifts")
    @Operation(summary = "Get all shifts for org nurses (for coverage management)")
    public ResponseEntity<ApiResponse<List<ShiftCoverageResponse>>> getOrgShifts(
            @PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(supervisorService.getOrgShifts(orgUserId)));
    }

    @PatchMapping("/{orgUserId}/shifts/{shiftId}/cover")
    @Operation(summary = "Assign a covering nurse to a shift")
    public ResponseEntity<ApiResponse<ShiftCoverageResponse>> assignShiftCoverage(
            @PathVariable Long orgUserId,
            @PathVariable Long shiftId,
            @RequestParam Long coveringNurseUserId) {
        return ResponseEntity.ok(ApiResponse.success("Coverage assigned",
                supervisorService.assignShiftCoverage(orgUserId, shiftId, coveringNurseUserId)));
    }

    // ── Handoff Notes (AC 16.4) ───────────────────────────────────────────────

    @PostMapping("/{orgUserId}/handoff")
    @Operation(summary = "Send a handoff note to a nurse")
    public ResponseEntity<ApiResponse<HandoffNoteResponse>> sendHandoff(
            @PathVariable Long orgUserId,
            @RequestBody HandoffNoteRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Handoff note sent",
                supervisorService.sendHandoff(orgUserId, req)));
    }

    @GetMapping("/{orgUserId}/handoffs")
    @Operation(summary = "Get all handoff notes sent by this org supervisor")
    public ResponseEntity<ApiResponse<List<HandoffNoteResponse>>> getHandoffs(
            @PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(supervisorService.getHandoffs(orgUserId)));
    }

    // ── Escalations (AC 16.7) ─────────────────────────────────────────────────

    @PostMapping("/{orgUserId}/escalate")
    @Operation(summary = "Raise an escalation issue")
    public ResponseEntity<ApiResponse<EscalationResponse>> createEscalation(
            @PathVariable Long orgUserId,
            @RequestBody EscalationRequest req) {
        return ResponseEntity.ok(ApiResponse.success("Escalation raised",
                supervisorService.createEscalation(orgUserId, req)));
    }

    @GetMapping("/{orgUserId}/escalations")
    @Operation(summary = "Get all escalations for this org")
    public ResponseEntity<ApiResponse<List<EscalationResponse>>> getEscalations(
            @PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(supervisorService.getEscalations(orgUserId)));
    }

    @PatchMapping("/{orgUserId}/escalations/{escalationId}/resolve")
    @Operation(summary = "Mark an escalation as resolved")
    public ResponseEntity<ApiResponse<EscalationResponse>> resolveEscalation(
            @PathVariable Long orgUserId,
            @PathVariable Long escalationId) {
        return ResponseEntity.ok(ApiResponse.success("Escalation resolved",
                supervisorService.resolveEscalation(orgUserId, escalationId)));
    }
}
