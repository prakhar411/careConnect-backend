package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "Please provide a valid <identifier>")
    private String identifier;

    @NotBlank(message = "Please provide a valid <password>")
    private String password;

    private String role;
}
