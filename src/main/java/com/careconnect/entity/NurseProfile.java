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

    @Column(unique = true)
    private String licenseNumber;

    private String phone;
    private String specialization;
    private String education;
    private String address;
    private Integer experienceYears;
    private String expertise;
    private String previousEmployment;

    @Column(name = "professional_references", columnDefinition = "TEXT")
    private String references;

    private String availability;
    private Double rating;
    private String profileStatus;
}
