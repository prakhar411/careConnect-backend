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

    // Who paid
    private String paidBy;        // PATIENT or ORGANIZATION
    private Long paidByUserId;

    // Amount fields
    private BigDecimal amount;    // net pay (after deductions)
    private BigDecimal grossAmount;
    private BigDecimal taxDeduction;   // TDS
    private BigDecimal pfDeduction;
    private BigDecimal esiDeduction;

    // Payment details
    private String paymentMethod;      // UPI or BANK_TRANSFER
    private String paymentStructure;   // SHIFT or MONTHLY_SALARY
    private BigDecimal hoursWorked;
    private String salaryMonth;
    private String description;
    private PaymentStatus status;
    private LocalDateTime paymentDate;

    // Nurse bank details (shown to payer)
    private String nurseUpiId;
    private String nurseBankAccount;
    private String nurseIfsc;
    private String nurseBankName;
    private String nursePreferredPaymentMode;

    // Context info
    private String patientName;        // for nurse's patient payment view
    private String orgName;            // for nurse's salary view
    private String appointmentCareNeeds;
}
