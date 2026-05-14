package com.careconnect.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shifts")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    // Date of the shift (patient selects)
    @Column(nullable = false)
    private LocalDate shiftDate;

    // Original rate — from nurse's salaryExpectation (AppointmentApplication bid)
    @Column(nullable = false)
    private BigDecimal originalRate;

    // Negotiated rate — patient's counter-proposal (null = no negotiation)
    private BigDecimal negotiatedRate;

    // NONE (no negotiation) | PENDING | ACCEPTED | REJECTED
    @Builder.Default
    private String negotiationStatus = "NONE";

    private String notes;

    // PENDING_CONFIRMATION → CONFIRMED / REJECTED
    @Builder.Default
    private String status = "PENDING_CONFIRMATION";

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime patientMarkedAt;

    private LocalDateTime nurseConfirmedAt;
}
