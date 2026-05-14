package com.careconnect.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShiftPaymentRequest {

    @NotNull(message = "Please provide a valid <appointmentId>")
    private Long appointmentId;

    @NotNull(message = "Please provide a valid <ratePerShift>")
    @DecimalMin(value = "1.0", message = "Please provide a valid <ratePerShift>")
    private BigDecimal ratePerShift;

    private String notes;
}
