package com.careconnect.entity;

import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "jobs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organization_id", nullable = false)
    private Organization organization;

    @Column(nullable = false)
    private String jobTitle;

    private String department;
    private String location;

    @Enumerated(EnumType.STRING)
    private JobType jobType;

    private String specialization;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String shiftDetails;
    private String patientAcuity;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Builder.Default
    private String priority = "Normal";

    private LocalDate deadline;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private JobStatus status = JobStatus.ACTIVE;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
