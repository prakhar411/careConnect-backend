package com.careconnect.dto.response;

import com.careconnect.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ApplicationResponse {
    private Long id;
    private Long nurseId;
    private String nurseName;
    private String nursePhone;
    private String nurseEmail;
    private String nurseSpecialization;
    private Integer nurseExperience;
    private String nurseEducation;
    private String nurseExpertise;
    private String nurseLicenseNumber;
    private String nurseAvailability;
    private String nursePreviousEmployment;
    private String nurseReferences;
    private Long jobId;
    private String jobTitle;
    private String organizationName;
    private String coverNote;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
    private List<CredentialResponse> nurseCredentials;

    // Nurse bank details (for org salary processing)
    private String nurseUpiId;
    private String nurseBankAccount;
    private String nurseIfsc;
    private String nurseBankName;
    private String nursePreferredPaymentMode;
}
