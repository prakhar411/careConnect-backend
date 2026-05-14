package com.careconnect.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ShiftRequest {

    @NotNull(message = "Please provide a valid <appointmentId>")
    private Long appointmentId;

    // String format YYYY-MM-DD — avoids Jackson LocalDate serialization issues
    @NotBlank(message = "Please provide a valid <shiftDate>")
    private String shiftDate;

    // Optional — patient's counter-proposal if different from agreedRatePerShift
    @DecimalMin(value = "1.0", message = "Proposed rate must be at least ₹1")
    private BigDecimal negotiatedRate;

    private String notes;
}
