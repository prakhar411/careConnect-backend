package com.careconnect.controller;

import com.careconnect.dto.request.JobRequest;
import com.careconnect.dto.response.ApiResponse;
import com.careconnect.dto.response.JobResponse;
import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import com.careconnect.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting and search endpoints")
public class JobController {

    private final JobService jobService;

    @PostMapping("/org/{orgUserId}")
    @Operation(summary = "Create a new job posting")
    public ResponseEntity<ApiResponse<JobResponse>> createJob(
            @PathVariable Long orgUserId,
            @Valid @RequestBody JobRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Job posted successfully", jobService.createJob(orgUserId, request)));
    }

    @GetMapping
    @Operation(summary = "Search / browse all active jobs")
    public ResponseEntity<ApiResponse<List<JobResponse>>> searchJobs(
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) JobType jobType) {
        return ResponseEntity.ok(ApiResponse.success(jobService.searchJobs(specialization, location, jobType)));
    }

    @GetMapping("/org/{orgUserId}")
    @Operation(summary = "Get jobs posted by a specific organization")
    public ResponseEntity<ApiResponse<List<JobResponse>>> getMyJobs(@PathVariable Long orgUserId) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobsByOrganization(orgUserId)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get job details by ID")
    public ResponseEntity<ApiResponse<JobResponse>> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(jobService.getJobById(id)));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update job status")
    public ResponseEntity<ApiResponse<Void>> updateStatus(@PathVariable Long id, @RequestParam JobStatus status) {
        jobService.updateJobStatus(id, status);
        return ResponseEntity.ok(ApiResponse.success("Status updated", null));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a job posting")
    public ResponseEntity<ApiResponse<Void>> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok(ApiResponse.success("Job deleted", null));
    }
}
