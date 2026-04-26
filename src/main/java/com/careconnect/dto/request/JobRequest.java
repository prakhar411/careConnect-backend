package com.careconnect.dto.request;

import com.careconnect.enums.JobType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobRequest {

    @NotBlank(message = "Please provide a valid <jobTitle>")
    @Size(min = 3, message = "Please provide a valid <jobTitle>")
    private String jobTitle;

    @NotBlank(message = "Please provide a valid <department>")
    private String department;

    @NotBlank(message = "Please provide a valid <location>")
    private String location;

    @NotNull(message = "Please provide a valid <jobType>")
    private JobType jobType;

    private String specialization;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private String shiftDetails;
    private String patientAcuity;

    @NotBlank(message = "Please provide a valid <description>")
    @Size(min = 20, message = "Please provide a valid <description>")
    private String description;

    private String priority;
    private LocalDate deadline;
}
