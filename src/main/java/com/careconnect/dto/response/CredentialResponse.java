package com.careconnect.dto.response;

import com.careconnect.enums.CredentialStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CredentialResponse {
    private Long id;
    private Long nurseId;
    private String nurseName;
    private String credentialType;
    private String issuedBy;
    private LocalDate issuedDate;
    private LocalDate expiryDate;
    private CredentialStatus status;
    private LocalDateTime createdAt;
}
