package com.careconnect.dto.response;

import com.careconnect.enums.ComplianceStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class ComplianceResponse {
    private Long id;
    private Long organizationId;
    private String nurseName;
    private String requirement;
    private LocalDate dueDate;
    private ComplianceStatus status;
    private String notes;
    private LocalDateTime createdAt;
}
