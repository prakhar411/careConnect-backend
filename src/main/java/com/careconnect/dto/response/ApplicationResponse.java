package com.careconnect.dto.response;

import com.careconnect.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;
    private Long nurseId;
    private String nurseName;
    private String nurseSpecialization;
    private Integer nurseExperience;
    private Long jobId;
    private String jobTitle;
    private String organizationName;
    private String coverNote;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
