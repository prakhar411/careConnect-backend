package com.careconnect.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class EscalationResponse {
    private Long   id;
    private String issueType;
    private String entityType;
    private Long   entityId;
    private String entityName;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
