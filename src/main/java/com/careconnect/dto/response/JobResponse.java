package com.careconnect.dto.response;

import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String facilityType;
    private String jobTitle;
    private String department;
    private String location;
    private JobType jobType;
    private String specialization;
    private String patientPopulation;
    private Integer openings;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String shiftDetails;
    private String workingConditions;
    private String benefits;
    private String description;
    private String priority;
    private LocalDateTime deadline;
    private JobStatus status;
    private LocalDateTime createdAt;
}
