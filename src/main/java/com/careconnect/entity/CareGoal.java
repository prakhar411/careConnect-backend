package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "care_goals")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CareGoal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientUserId;

    private Long addedByNurseUserId;
    private String addedByNurseName;

    @Column(nullable = false)
    private String goalText;

    private LocalDate targetDate;

    @Builder.Default
    private String status = "PENDING"; // PENDING | IN_PROGRESS | ACHIEVED

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
