package com.careconnect.service;

import com.careconnect.dto.request.JobRequest;
import com.careconnect.dto.response.JobResponse;
import com.careconnect.entity.Job;
import com.careconnect.entity.Organization;
import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.JobRepository;
import com.careconnect.repository.OrganizationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;
    private final OrganizationRepository organizationRepository;
    private final ModelMapper modelMapper;

    @Transactional
    public JobResponse createJob(Long userId, JobRequest request) {
        Organization org = organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", userId));

        Job job = Job.builder()
                .organization(org)
                .jobTitle(request.getJobTitle())
                .department(request.getDepartment())
                .location(request.getLocation())
                .jobType(request.getJobType())
                .specialization(request.getSpecialization())
                .salaryMin(request.getSalaryMin())
                .salaryMax(request.getSalaryMax())
                .shiftDetails(request.getShiftDetails())
                .patientAcuity(request.getPatientAcuity())
                .description(request.getDescription())
                .priority(request.getPriority())
                .deadline(request.getDeadline())
                .build();

        jobRepository.save(job);
        log.info("Job created: {} by org {}", request.getJobTitle(), org.getOrgName());
        return toResponse(job);
    }

    public List<JobResponse> searchJobs(String specialization, String location, JobType jobType) {
        return jobRepository.searchJobs(specialization, location, jobType)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<JobResponse> getJobsByOrganization(Long userId) {
        Organization org = organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", userId));
        return jobRepository.findByOrganizationId(org.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public JobResponse getJobById(Long id) {
        return toResponse(jobRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job", id)));
    }

    @Transactional
    public void updateJobStatus(Long id, JobStatus status) {
        Job job = jobRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Job", id));
        job.setStatus(status);
        jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    private JobResponse toResponse(Job job) {
        return JobResponse.builder()
                .id(job.getId())
                .organizationId(job.getOrganization().getId())
                .organizationName(job.getOrganization().getOrgName())
                .jobTitle(job.getJobTitle())
                .department(job.getDepartment())
                .location(job.getLocation())
                .jobType(job.getJobType())
                .specialization(job.getSpecialization())
                .salaryMin(job.getSalaryMin())
                .salaryMax(job.getSalaryMax())
                .description(job.getDescription())
                .priority(job.getPriority())
                .deadline(job.getDeadline())
                .status(job.getStatus())
                .createdAt(job.getCreatedAt())
                .build();
    }

    public List<JobResponse> fallbackList(Throwable t) {
        log.error("Circuit breaker: job service unavailable — {}", t.getMessage());
        return List.of();
    }
}
