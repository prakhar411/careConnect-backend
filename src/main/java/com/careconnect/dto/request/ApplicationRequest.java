package com.careconnect.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {

    @NotNull(message = "Please provide a valid <jobId>")
    private Long jobId;

    private String coverNote;
}
