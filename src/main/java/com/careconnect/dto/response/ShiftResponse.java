package com.careconnect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ShiftResponse {
    private Long id;
    private Long appointmentId;
    private String appointmentCareNeeds;

    // Patient info
    private String patientName;
    private Long   patientUserId;

    // Nurse info
    private String nurseName;
    private Long   nurseUserId;

    // Shift details
    private LocalDate    shiftDate;
    private BigDecimal   originalRate;      // nurse ka bid rate
    private BigDecimal   negotiatedRate;    // patient ka counter (null = no negotiation)
    private String       negotiationStatus; // NONE | PENDING | ACCEPTED | REJECTED
    private BigDecimal   finalRate;         // actual rate used for payment
    private String       notes;
    private String       status;            // PENDING_CONFIRMATION | CONFIRMED | REJECTED
    private LocalDateTime patientMarkedAt;
    private LocalDateTime nurseConfirmedAt;

    // Appointment date context
    private LocalDateTime appointmentStartDate;
    private LocalDateTime appointmentEndDate;
}
