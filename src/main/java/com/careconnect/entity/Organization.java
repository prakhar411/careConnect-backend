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

    private String contactPerson;
    private String designation;
    private String phone;
    private String address;
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

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
