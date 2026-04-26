package com.careconnect.service;

import com.careconnect.dto.request.JobRequest;
import com.careconnect.dto.response.JobResponse;
import com.careconnect.entity.Job;
import com.careconnect.entity.Organization;
import com.careconnect.enums.JobType;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.JobRepository;
import com.careconnect.repository.OrganizationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JobServiceTest {

    @Mock JobRepository jobRepository;
    @Mock OrganizationRepository organizationRepository;
    @Mock ModelMapper modelMapper;

    @InjectMocks JobService jobService;

    private Organization organization;
    private JobRequest jobRequest;

    @BeforeEach
    void setUp() {
        organization = Organization.builder()
                .id(1L).orgName("City Hospital").build();

        jobRequest = new JobRequest();
        jobRequest.setJobTitle("ICU Nurse");
        jobRequest.setDepartment("ICU");
        jobRequest.setLocation("Mumbai");
        jobRequest.setJobType(JobType.PERMANENT);
        jobRequest.setDescription("ICU nursing position requiring 3+ years experience");
    }

    @Test
    void createJob_shouldSucceed_whenOrganizationExists() {
        when(organizationRepository.findByUserId(anyLong())).thenReturn(Optional.of(organization));
        when(jobRepository.save(any(Job.class))).thenAnswer(inv -> {
            Job j = inv.getArgument(0);
            j.setId(1L);
            return j;
        });

        JobResponse response = jobService.createJob(1L, jobRequest);

        assertNotNull(response);
        assertEquals("ICU Nurse", response.getJobTitle());
        verify(jobRepository).save(any(Job.class));
    }

    @Test
    void createJob_shouldThrow_whenOrganizationNotFound() {
        when(organizationRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> jobService.createJob(99L, jobRequest));
    }

    @Test
    void searchJobs_shouldReturnList() {
        Job job = Job.builder().id(1L).jobTitle("ICU Nurse").organization(organization).build();
        when(jobRepository.searchJobs(any(), any(), any())).thenReturn(List.of(job));

        List<JobResponse> result = jobService.searchJobs("ICU", "Mumbai", JobType.PERMANENT);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
    }

    @Test
    void deleteJob_shouldCallRepository() {
        doNothing().when(jobRepository).deleteById(anyLong());

        jobService.deleteJob(1L);

        verify(jobRepository).deleteById(1L);
    }
}
