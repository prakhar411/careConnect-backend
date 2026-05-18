package com.careconnect.dto.request;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class HandoffNoteRequest {
    private Long   nurseUserId;
    private Long   jobId;
    private String note;
}
