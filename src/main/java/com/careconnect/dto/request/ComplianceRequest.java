package com.careconnect.dto.request;

import com.careconnect.enums.ComplianceStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class ComplianceRequest {

    @NotBlank(message = "Please provide a valid <nurseName>")
    private String nurseName;

    @NotBlank(message = "Please provide a valid <requirement>")
    private String requirement;

    @NotNull(message = "Please provide a valid <dueDate>")
    private LocalDate dueDate;

    private ComplianceStatus status;
    private String notes;
}
