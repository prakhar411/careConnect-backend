package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ProviderNoteRequest {
    @NotBlank(message = "Note content is required")
    private String content;
    private String noteType;
    private Long authorNurseUserId;
    private String authorName;
    private String authorRole;
}
