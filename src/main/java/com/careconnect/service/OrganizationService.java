package com.careconnect.service;

import com.careconnect.dto.request.UpdateOrgRequest;
import com.careconnect.entity.Organization;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.enums.AppointmentStatus;
import com.careconnect.enums.JobStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Transactional
    public Organization updateProfile(Long userId, UpdateOrgRequest request) {
        Organization org = organizationRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", userId));

        org.setOrgName(request.getOrgName());
        org.setOrgType(request.getOrgType());
        org.setContactPerson(request.getContactPerson());
        org.setDesignation(request.getDesignation());
        org.setPhone(request.getPhone());
        org.setAddress(request.getAddress());
        org.setCity(request.getCity());
        org.setState(request.getState());
        org.setPincode(request.getPincode());
        org.setWebsite(request.getWebsite());
        org.setBedCapacity(request.getBedCapacity());
        org.setSpecializations(request.getSpecializations());
        org.setAccreditation(request.getAccreditation());

        return organizationRepository.save(org);
    }

    public Map<String, Long> getDashboardStats(Long orgUserId) {
        return Map.of(
                "totalJobs",            jobRepository.countByOrgUserId(orgUserId),
                "activeJobs",           jobRepository.countByOrgUserIdAndStatus(orgUserId, JobStatus.ACTIVE),
                "pendingApplications",  applicationRepository.countByOrgUserIdAndStatus(orgUserId, ApplicationStatus.PENDING),
                "approvedApplications", applicationRepository.countByOrgUserIdAndStatus(orgUserId, ApplicationStatus.APPROVED),
                "hiredNurses",          applicationRepository.countHiredNursesByOrgUserId(orgUserId)
        );
    }
}
