package com.careconnect.dto.response;

import com.careconnect.enums.AppointmentStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class AppointmentResponse {
    private Long id;
    private Long patientId;
    private String patientName;
    private Long nurseId;
    private String nurseName;
    private LocalDateTime appointmentDate;
    private String careNeeds;
    private String duration;
    private AppointmentStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
