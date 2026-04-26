package com.careconnect.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class PaymentRequest {

    @NotNull(message = "Please provide a valid <amount>")
    @DecimalMin(value = "0.01", message = "Please provide a valid <amount>")
    private BigDecimal amount;

    private Long appointmentId;
    private String paymentMethod;
    private String paymentStructure;
    private BigDecimal hoursWorked;
    private String description;
}
