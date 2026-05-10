package com.careconnect.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PatientProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    private String firstName;
    private String middleName;
    private String lastName;

    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;
    private String bloodGroup;

    private String phoneCountryCode;
    private String phone;

    // Structured address
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;
    private String city;
    private String state;
    private String pincode;

    // Legacy flat address field (kept for backward compat)
    private String address;
    private String emergencyContact;
    private String emergencyContactPhone;
    private String medicalHistory;
    private String allergies;
    private String currentMedications;
}
