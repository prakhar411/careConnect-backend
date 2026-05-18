package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CompleteTrainingRequest {
    @NotBlank private String courseName;
    private String  category;
    private Integer creditPoints;
    private String  source;     // STATIC | ORG
    private Long    orgCourseId;
}
