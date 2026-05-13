package com.careconnect.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "Please provide a valid <appointmentDate>")
    @Future(message = "Please provide a valid <appointmentDate>")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Please provide a valid <careNeeds>")
    private String careNeeds;

    private Long nurseId;
    private String requiredSkills;
    private String duration;
    private String notes;

    // Booking metadata
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
    private LocalDateTime applicationDeadline;
}
