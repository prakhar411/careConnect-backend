package com.careconnect.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class HandoffNoteResponse {
    private Long   id;
    private Long   recipientNurseUserId;
    private String nurseName;
    private Long   jobId;
    private String jobTitle;
    private String note;
    private LocalDateTime sentAt;
}
