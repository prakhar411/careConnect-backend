package com.careconnect.dto.response;

import com.careconnect.enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private Long patientUserId;
    private String patientName;

    // Nurse fields — null when no nurse assigned yet
    private Long nurseId;
    private Long nurseUserId;
    private String nurseName;
    private String nursePhone;
    private String nurseEmail;
    private String nurseSpecialization;
    private Integer nurseExperience;
    private String nurseEducation;
    private String nurseExpertise;
    private String nurseLicenseNumber;
    private String nurseAvailability;
    private Double nurseRating;

    private LocalDateTime appointmentDate;
    private String careNeeds;
    private String requiredSkills;
    private String duration;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
    private Integer applicantCount;

    // Patient contact (submitted from booking form)
    private String bookingFor;
    private String patientFirstName;
    private String patientMiddleName;
    private String patientLastName;
    private String patientEmail;
    private String patientPhone;
    private String patientPhoneCountryCode;
    private String patientAddressLine1;
    private String patientAddressLine2;
    private String patientLandmark;
    private String patientCity;
    private String patientState;
    private String patientPincode;

    // Schedule & preferences
    private String scheduleType;
    private String scheduleDays;
    private String priority;
    private String genderPreference;
    private String languagePreference;
    private String specialization;
    private String medicalCondition;
    private String mobilityLevel;
    private String dietRequirements;
    private java.time.LocalDateTime applicationDeadline;
}
