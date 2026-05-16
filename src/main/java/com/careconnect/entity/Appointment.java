package com.careconnect.entity;

import com.careconnect.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private PatientProfile patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id")
    private NurseProfile nurse;

    @Column(nullable = false)
    private LocalDateTime appointmentDate;

    private LocalDateTime endDate;
    private String careNeeds;
    private String requiredSkills;
    private String duration;

    @Column(columnDefinition = "TEXT")
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

    // Agreed rate per shift — copied from nurse's AppointmentApplication.salaryExpectation when bid is accepted
    private java.math.BigDecimal agreedRatePerShift;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private AppointmentStatus status = AppointmentStatus.PENDING;

    // Reconciliation — populated by scheduler when endDate passes
    // PENDING | NURSE_CONFIRMED | PATIENT_CONFIRMED | AGREED | DISPUTED
    private String reconciliationStatus;
    private Integer expectedShifts;
    private LocalDateTime nurseReconciliationAt;
    private LocalDateTime patientReconciliationAt;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
