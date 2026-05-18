package com.careconnect.dto.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ShiftCoverageResponse {
    private Long   shiftId;
    private Long   appointmentId;
    private LocalDate shiftDate;
    private String status;
    private BigDecimal originalRate;

    // Original nurse
    private Long   nurseProfileId;
    private Long   nurseUserId;
    private String nurseName;
    private String nurseSpecialization;

    // Patient
    private String patientName;

    // Covering nurse (null if not yet covered)
    private Long   coveringNurseUserId;
    private String coveringNurseName;

    private LocalDateTime markedAt;
}
