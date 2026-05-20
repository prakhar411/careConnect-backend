package com.careconnect.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "organizations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Organization {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(nullable = false)
    private String orgName;

    private String orgType;

    @Column(unique = true)
    private String regNumber;

    private String licenseNumber;
    private String contactPerson;
    private String contactFirstName;
    private String contactMiddleName;
    private String contactLastName;
    private String designation;
    private String contact2FirstName;
    private String contact2MiddleName;
    private String contact2LastName;
    private String contact2Email;
    private String contact2Phone;
    private String contact2Designation;
    private String phone;
    private String address;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;
    private String city;
    private String state;
    private String pincode;
    private String website;

    private Integer bedCapacity;

    @Column(columnDefinition = "TEXT")
    private String specializations;

    private String accreditation;

    @Builder.Default
    private String status = "ACTIVE";

    @Builder.Default
    private Boolean verifiedByAdmin = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
