package com.careconnect.controller;

import com.careconnect.dto.request.CompleteTrainingRequest;
import com.careconnect.dto.request.OrgCourseRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.entity.OrgCourse;
import com.careconnect.entity.TrainingRecord;
import com.careconnect.service.TrainingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/training")
@RequiredArgsConstructor
@Tag(name = "Training", description = "Professional development and training endpoints")
public class TrainingController {

    private final TrainingService trainingService;

    // ── Org endpoints ────────────────────────────────────────────────────────

    @PostMapping("/org/{orgUserId}/courses")
    @Operation(summary = "Org adds a mandatory training course")
    public ResponseEntity<ApiResponse<OrgCourse>> addCourse(
            @PathVariable Long orgUserId,
            @Valid @RequestBody OrgCourseRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course added", trainingService.addOrgCourse(orgUserId, request)));
    }

    @GetMapping("/org/{orgUserId}/courses")
    @Operation(summary = "Get all courses added by an org")
    public ResponseEntity<ApiResponse<List<OrgCourse>>> getOrgCourses(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getOrgCourses(orgUserId)));
    }

    @DeleteMapping("/org/{orgUserId}/courses/{courseId}")
    @Operation(summary = "Org deletes a training course")
    public ResponseEntity<ApiResponse<Void>> deleteCourse(
            @PathVariable Long orgUserId, @PathVariable Long courseId) {
        trainingService.deleteOrgCourse(courseId, orgUserId);
        return ResponseEntity.ok(ApiResponse.success("Course deleted", null));
    }

    @GetMapping("/courses")
    @Operation(summary = "Get all org-added courses (visible to all nurses)")
    public ResponseEntity<ApiResponse<List<OrgCourse>>> getAllCourses() {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getAllOrgCourses()));
    }

    // ── Nurse endpoints ──────────────────────────────────────────────────────

    @PostMapping("/nurse/{nurseUserId}/complete")
    @Operation(summary = "Nurse marks a course as completed")
    public ResponseEntity<ApiResponse<TrainingRecord>> complete(
            @PathVariable Long nurseUserId,
            @Valid @RequestBody CompleteTrainingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Course completed", trainingService.completeTraining(nurseUserId, request)));
    }

    @GetMapping("/nurse/{nurseUserId}")
    @Operation(summary = "Get nurse training completion history")
    public ResponseEntity<ApiResponse<List<TrainingRecord>>> getNurseHistory(
            @PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getNurseCompletions(nurseUserId)));
    }

    @GetMapping("/nurse/{nurseUserId}/summary")
    @Operation(summary = "Get nurse training summary (credits, counts)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@PathVariable Long nurseUserId) {
        return ResponseEntity.ok(ApiResponse.success(trainingService.getNurseSummary(nurseUserId)));
    }
}
