package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "escalations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Escalation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long orgUserId;

    // NURSE_PERFORMANCE | SCHEDULING_CONFLICT | PATIENT_CONCERN | QUALITY_ISSUE | OTHER
    @Column(nullable = false)
    private String issueType;

    // NURSE | JOB  (nullable if general issue)
    private String entityType;
    private Long   entityId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    // OPEN | RESOLVED
    @Builder.Default
    private String status = "OPEN";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
}
