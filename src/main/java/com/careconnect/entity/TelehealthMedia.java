package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "telehealth_media")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TelehealthMedia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long nurseUserId;

    private String nurseName;

    @Column(nullable = false)
    private Long patientUserId;

    private String patientName;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String fileName;

    private String originalFileName;

    private String fileType;

    @Column(columnDefinition = "TEXT")
    private String description;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
