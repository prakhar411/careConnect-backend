package com.careconnect.entity;

import com.careconnect.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "nurse_id", nullable = false)
    private NurseProfile nurse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id")
    private Appointment appointment;

    @Column(nullable = false)
    private BigDecimal amount;

    private String paymentMethod;
    private String paymentStructure;
    private BigDecimal hoursWorked;
    private BigDecimal taxDeduction;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Who paid — PATIENT or ORGANIZATION
    private String paidBy;

    // userId of the payer (patientUserId for SHIFT, orgUserId for MONTHLY_SALARY)
    private Long paidByUserId;

    // For org monthly salary — e.g. "May 2026"
    private String salaryMonth;

    // Salary breakdown (org payments)
    private BigDecimal grossAmount;
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private PaymentStatus status = PaymentStatus.PENDING;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime paymentDate;
}
