package com.careconnect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JobAssignmentResponse {
    private Long   jobId;
    private String jobTitle;
    private String department;
    private String jobType;
    private String jobStatus;
    private String location;
    private String specialization;
    private Integer openings;
    private LocalDateTime deadline;

    // Assigned nurse (null = unassigned)
    private Long   assignedNurseId;
    private Long   assignedNurseUserId;
    private String assignedNurseName;
    private String assignedNurseSpecialization;
    private Long   applicationId;

    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}
