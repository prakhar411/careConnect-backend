package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "vital_signs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class VitalSign {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long patientUserId;

    @Column(nullable = false)
    private Long nurseUserId;

    private Long appointmentId;

    private String bloodPressure;
    private Integer pulseRate;
    private Double temperature;
    private Integer spo2;
    private Double weight;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime recordedAt;
}
