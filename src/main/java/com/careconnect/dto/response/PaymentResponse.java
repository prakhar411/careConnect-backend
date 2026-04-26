package com.careconnect.dto.response;

import com.careconnect.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaymentResponse {
    private Long id;
    private Long nurseId;
    private String nurseName;
    private Long appointmentId;
    private BigDecimal amount;
    private String paymentMethod;
    private String paymentStructure;
    private BigDecimal hoursWorked;
    private BigDecimal taxDeduction;
    private String description;
    private PaymentStatus status;
    private LocalDateTime paymentDate;
}
