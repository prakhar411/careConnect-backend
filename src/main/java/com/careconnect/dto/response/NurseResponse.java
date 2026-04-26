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
    private Double rating;
    private String profileStatus;
}
