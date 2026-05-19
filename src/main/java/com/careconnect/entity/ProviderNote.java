package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "provider_notes")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ProviderNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientUserId;

    private Long authorNurseUserId;

    @Column(nullable = false)
    private String authorName;

    @Builder.Default
    private String authorRole = "Nurse";

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    @Builder.Default
    private String noteType = "CLINICAL_UPDATE"; // CLINICAL_UPDATE | REFERRAL | ALERT | FOLLOW_UP

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
