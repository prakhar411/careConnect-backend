package com.careconnect.service;

import com.careconnect.dto.request.ApplicationRequest;
import com.careconnect.dto.response.ApplicationResponse;
import com.careconnect.entity.Job;
import com.careconnect.entity.NurseApplication;
import com.careconnect.entity.NurseProfile;
import com.careconnect.entity.Organization;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.JobRepository;
import com.careconnect.repository.NurseApplicationRepository;
import com.careconnect.repository.NurseProfileRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationServiceTest {

    @Mock NurseApplicationRepository applicationRepository;
    @Mock NurseProfileRepository nurseProfileRepository;
    @Mock JobRepository jobRepository;

    @InjectMocks ApplicationService applicationService;

    private NurseProfile nurse;
    private Job job;
    private ApplicationRequest applicationRequest;

    @BeforeEach
    void setUp() {
        Organization org = Organization.builder().id(1L).orgName("City Hospital").build();
        nurse = NurseProfile.builder().id(1L).fullName("Jane Nurse").specialization("ICU").build();
        job = Job.builder().id(1L).jobTitle("ICU Nurse").organization(org).build();

        applicationRequest = new ApplicationRequest();
        applicationRequest.setJobId(1L);
        applicationRequest.setCoverNote("Experienced ICU nurse ready to join.");
    }

    @Test
    void apply_shouldSucceed_whenNotAlreadyApplied() {
        when(nurseProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(nurse));
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(job));
        when(applicationRepository.existsByNurseIdAndJobId(anyLong(), anyLong())).thenReturn(false);
        when(applicationRepository.save(any(NurseApplication.class))).thenAnswer(inv -> {
            NurseApplication a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        ApplicationResponse response = applicationService.apply(1L, applicationRequest);

        assertNotNull(response);
        assertEquals(ApplicationStatus.PENDING, response.getStatus());
        verify(applicationRepository).save(any());
    }

    @Test
    void apply_shouldThrow_whenAlreadyApplied() {
        when(nurseProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(nurse));
        when(jobRepository.findById(anyLong())).thenReturn(Optional.of(job));
        when(applicationRepository.existsByNurseIdAndJobId(anyLong(), anyLong())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> applicationService.apply(1L, applicationRequest));
    }

    @Test
    void updateStatus_shouldApprove() {
        NurseApplication app = NurseApplication.builder()
                .id(1L).nurse(nurse).job(job).status(ApplicationStatus.PENDING).build();

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any())).thenReturn(app);

        ApplicationResponse response = applicationService.updateStatus(1L, ApplicationStatus.APPROVED);

        assertEquals(ApplicationStatus.APPROVED, response.getStatus());
    }

    @Test
    void getByNurse_shouldReturnApplicationList() {
        NurseApplication app = NurseApplication.builder()
                .id(1L).nurse(nurse).job(job).status(ApplicationStatus.PENDING).build();

        when(nurseProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(nurse));
        when(applicationRepository.findByNurseId(anyLong())).thenReturn(List.of(app));

        List<ApplicationResponse> result = applicationService.getByNurse(1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }
}
