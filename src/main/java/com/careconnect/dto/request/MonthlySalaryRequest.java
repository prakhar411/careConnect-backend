package com.careconnect.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MonthlySalaryRequest {

    @NotNull(message = "Please provide a valid <nurseUserId>")
    private Long nurseUserId;

    @NotBlank(message = "Please provide a valid <salaryMonth>")
    private String salaryMonth; // e.g. "May 2026"

    @NotNull(message = "Please provide a valid <baseSalary>")
    @DecimalMin(value = "1000.0", message = "Minimum base salary is ₹1,000")
    private BigDecimal baseSalary;

    private BigDecimal hra;
    private BigDecimal travelAllowance;
    private BigDecimal otherAllowances;
}
