package com.careconnect.controller;

import com.careconnect.dto.request.TeamMemberRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.TeamMemberResponse;
import com.careconnect.service.ManagementTeamService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@RequiredArgsConstructor
@Tag(name = "Management Team", description = "Organization management team endpoints")
public class ManagementTeamController {

    private final ManagementTeamService teamService;

    @GetMapping("/org/{orgUserId}")
    @Operation(summary = "Get management team for an organization")
    public ResponseEntity<ApiResponse<List<TeamMemberResponse>>> getByOrg(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(teamService.getByOrg(orgUserId)));
    }

    @PostMapping("/org/{orgUserId}")
    @Operation(summary = "Add a management team member")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> add(
            @PathVariable Long orgUserId,
            @Valid @RequestBody TeamMemberRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Member added", teamService.add(orgUserId, request)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Toggle team member active/inactive status")
    public ResponseEntity<ApiResponse<TeamMemberResponse>> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success("Status updated", teamService.toggleStatus(id)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Remove a team member")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        teamService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Member removed", null));
    }
}
