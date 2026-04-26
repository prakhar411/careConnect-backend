package com.careconnect.service;

import com.careconnect.entity.Organization;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.enums.AppointmentStatus;
import com.careconnect.enums.JobStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final JobRepository jobRepository;
    private final NurseApplicationRepository applicationRepository;
    private final UserRepository userRepository;

    public Organization getProfile(Long userId) {
        return organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", userId));
    }

    public Map<String, Long> getDashboardStats() {
        return Map.of(
                "totalJobs", jobRepository.count(),
                "activeJobs", jobRepository.countByStatus(JobStatus.ACTIVE),
                "pendingApplications", applicationRepository.countByStatus(ApplicationStatus.PENDING),
                "approvedApplications", applicationRepository.countByStatus(ApplicationStatus.APPROVED),
                "totalNurses", userRepository.countByRole(com.careconnect.enums.UserRole.NURSE),
                "totalPatients", userRepository.countByRole(com.careconnect.enums.UserRole.PATIENT)
        );
    }
}
