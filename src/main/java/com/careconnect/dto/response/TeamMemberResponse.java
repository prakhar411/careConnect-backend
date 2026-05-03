package com.careconnect.dto.response;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class TeamMemberResponse {
    private Long id;
    private String name;
    private String role;
    private String email;
    private String phone;
    private LocalDate joinDate;
    private String status;
    private LocalDateTime createdAt;
}
