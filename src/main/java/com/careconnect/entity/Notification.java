package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String message;

    private String type; // EMERGENCY_JOB, GENERAL, SHIFT, PAYMENT

    @Builder.Default
    private Boolean isRead = false;

    private Long relatedEntityId;
    private String relatedEntityType; // JOB, SHIFT, PAYMENT

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
