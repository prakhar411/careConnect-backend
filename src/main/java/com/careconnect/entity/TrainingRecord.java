package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "training_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TrainingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long nurseUserId;

    @Column(nullable = false)
    private String courseName;

    private String category;
    private Integer creditPoints;
    private String source;    // STATIC | ORG
    private Long orgCourseId; // set when source = ORG

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime completedAt;
}
