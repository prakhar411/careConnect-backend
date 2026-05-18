package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class OrgCourseRequest {
    @NotBlank private String title;
    private String description;
    @NotBlank private String category;
    @NotNull  private Integer creditPoints;
    private Boolean mandatory;
}
