package com.careconnect.dto.response;

import com.careconnect.enums.ApplicationStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentApplicationResponse {
    private Long id;

    // Appointment (request) info
    private Long appointmentId;
    private String patientName;
    private String careNeeds;
    private String requiredSkills;
    private String notes;
    private LocalDateTime appointmentDate;

    // Nurse info
    private Long nurseId;
    private String nurseName;
    private String nurseEmail;
    private String nursePhone;
    private String nurseSpecialization;
    private Integer nurseExperience;
    private String nurseEducation;
    private String nurseExpertise;
    private String nurseLicenseNumber;
    private String nurseAvailability;
    private String nursePreviousEmployment;
    private String nurseReferences;
    private Double nurseRating;

    // Bid info
    private Double salaryExpectation;
    private String note;
    private ApplicationStatus status;
    private LocalDateTime appliedAt;
}
