package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CredentialRequest {

    @NotBlank(message = "Please provide a valid <credentialType>")
    private String credentialType;

    @NotBlank(message = "Please provide a valid <issuedBy>")
    private String issuedBy;

    @NotNull(message = "Please provide a valid <issuedDate>")
    private LocalDate issuedDate;

    @NotNull(message = "Please provide a valid <expiryDate>")
    private LocalDate expiryDate;

    private String documentUrl;
}
