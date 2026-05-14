package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "nurse_profiles")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NurseProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String fullName;

    private String firstName;
    private String middleName;
    private String lastName;

    @Column(unique = true)
    private String licenseNumber;

    private String phone;
    private String phoneCountryCode;
    private String specialization;
    private String education;
    private String address;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;
    private String state;
    private String city;
    private String pincode;
    private Integer experienceYears;
    private String expertise;
    private String previousEmployment;

    @Column(name = "professional_references", columnDefinition = "TEXT")
    private String references;

    private String availability;
    private Double rating;
    private String profileStatus;

    // Payment / bank details
    private String upiId;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;
    private String preferredPaymentMode; // UPI or BANK_TRANSFER

    // Emergency availability
    @Builder.Default
    private Boolean availableForEmergency = false;
}
