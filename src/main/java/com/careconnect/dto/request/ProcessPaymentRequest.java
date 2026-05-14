package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProcessPaymentRequest {

    @NotNull(message = "Please provide a valid <appointmentId>")
    private Long appointmentId;

    @NotBlank(message = "Please select a payment method")
    private String paymentMethod; // UPI or BANK_TRANSFER
}
