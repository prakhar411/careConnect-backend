package com.careconnect.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NurseWorkloadResponse {
    private Long   nurseId;
    private Long   nurseUserId;
    private String nurseName;
    private String specialization;
    private Integer experienceYears;
    private String licenseNumber;
    private long   activeJobs;
    private long   completedJobs;
    private long   totalJobs;
    // AVAILABLE | ACTIVE | OVERLOADED
    private String workloadStatus;
}
