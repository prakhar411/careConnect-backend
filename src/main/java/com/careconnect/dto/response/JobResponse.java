package com.careconnect.dto.response;

import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class JobResponse {
    private Long id;
    private Long organizationId;
    private String organizationName;
    private String jobTitle;
    private String department;
    private String location;
    private JobType jobType;
    private String specialization;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String description;
    private String priority;
    private LocalDate deadline;
    private JobStatus status;
    private LocalDateTime createdAt;
}
