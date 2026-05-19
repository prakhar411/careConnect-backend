package com.careconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CareTeamRequest {
    @NotNull(message = "Team member ID is required")
    private Long teamMemberId;
    private Long appointmentId;
    private Long nurseUserId;
    private String nurseName;
}
