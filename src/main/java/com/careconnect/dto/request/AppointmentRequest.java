package com.careconnect.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AppointmentRequest {

    @NotNull(message = "Please provide a valid <appointmentDate>")
    @Future(message = "Please provide a valid <appointmentDate>")
    private LocalDateTime appointmentDate;

    @NotBlank(message = "Please provide a valid <careNeeds>")
    private String careNeeds;

    private Long nurseId;
    private String requiredSkills;
    private String duration;
    private String notes;
}
