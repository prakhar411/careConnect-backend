package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "org_courses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class OrgCourse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orgUserId;

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String category;      // ICU, Pediatric, Emergency, Compliance, General, etc.
    private Integer creditPoints;

    @Builder.Default
    private Boolean mandatory = false;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
