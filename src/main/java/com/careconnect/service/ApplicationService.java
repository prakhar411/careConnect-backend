package com.careconnect.service;

import com.careconnect.dto.request.ApplicationRequest;
import com.careconnect.dto.response.ApplicationResponse;
import com.careconnect.entity.Job;
import com.careconnect.entity.NurseApplication;
import com.careconnect.entity.NurseProfile;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.JobRepository;
import com.careconnect.repository.NurseApplicationRepository;
import com.careconnect.repository.NurseProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ApplicationService {

    private final NurseApplicationRepository applicationRepository;
    private final NurseProfileRepository nurseProfileRepository;
    private final JobRepository jobRepository;

    @Transactional
    public ApplicationResponse apply(Long nurseUserId, ApplicationRequest request) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResourceNotFoundException("Job", request.getJobId()));

        if (applicationRepository.existsByNurseIdAndJobId(nurse.getId(), job.getId())) {
            throw new BadRequestException("You have already applied for this job");
        }

        NurseApplication application = NurseApplication.builder()
                .nurse(nurse)
                .job(job)
                .coverNote(request.getCoverNote())
                .build();

        applicationRepository.save(application);
        log.info("Nurse {} applied for job {}", nurse.getFullName(), job.getJobTitle());
        return toResponse(application);
    }

    public List<ApplicationResponse> getByNurse(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return applicationRepository.findByNurseId(nurse.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getByJob(Long jobId) {
        return applicationRepository.findByJobId(jobId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<ApplicationResponse> getAll() {
        return applicationRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ApplicationResponse updateStatus(Long id, ApplicationStatus status) {
        NurseApplication app = applicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Application", id));
        app.setStatus(status);
        applicationRepository.save(app);
        return toResponse(app);
    }

    private ApplicationResponse toResponse(NurseApplication a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .nurseId(a.getNurse().getId())
                .nurseName(a.getNurse().getFullName())
                .nurseSpecialization(a.getNurse().getSpecialization())
                .nurseExperience(a.getNurse().getExperienceYears())
                .jobId(a.getJob().getId())
                .jobTitle(a.getJob().getJobTitle())
                .organizationName(a.getJob().getOrganization().getOrgName())
                .coverNote(a.getCoverNote())
                .status(a.getStatus())
                .appliedAt(a.getAppliedAt())
                .build();
    }

    public List<ApplicationResponse> fallbackList(Throwable t) {
        log.error("Circuit breaker: application service unavailable — {}", t.getMessage());
        return List.of();
    }
}
