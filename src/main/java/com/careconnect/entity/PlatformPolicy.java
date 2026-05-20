package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "platform_policies")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PlatformPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Builder.Default
    private String category = "GENERAL"; // HIPAA | GDPR | SAFETY | DATA | GENERAL

    private LocalDate effectiveDate;

    private Long addedByAdminId;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
