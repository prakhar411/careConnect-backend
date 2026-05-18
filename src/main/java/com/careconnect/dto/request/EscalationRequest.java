package com.careconnect.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class EscalationRequest {
    private String issueType;
    private String entityType;
    private Long   entityId;
    private String description;
}
