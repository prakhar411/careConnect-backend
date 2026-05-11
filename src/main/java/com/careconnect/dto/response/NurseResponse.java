package com.careconnect.dto.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class NurseResponse {
    private Long id;
    private Long userId;
    private String fullName;
    private String email;
    private String licenseNumber;
    private String phone;
    private String specialization;
    private String education;
    private String expertise;
    private Integer experienceYears;
    private String availability;
    private String address;
    private String firstName;
    private String middleName;
    private String lastName;
    private String phoneCountryCode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;
    private String state;
    private String city;
    private String pincode;
    private String references;
    private Double rating;
    private String profileStatus;
}
