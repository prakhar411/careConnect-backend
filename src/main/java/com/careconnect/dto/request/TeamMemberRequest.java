package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class TeamMemberRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Role is required")
    private String role;

    private String email;
    private String phone;
    private LocalDate joinDate;
}
