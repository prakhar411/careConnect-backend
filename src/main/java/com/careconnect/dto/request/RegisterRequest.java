package com.careconnect.dto.request;

import com.careconnect.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Please provide a valid <fullName>")
    @Size(min = 2, message = "Please provide a valid <fullName>")
    private String fullName;

    @NotBlank(message = "Please provide a valid <email>")
    @Email(message = "Please provide a valid <email>")
    private String email;

    @NotBlank(message = "Please provide a valid <password>")
    @Size(min = 12, message = "Password must be at least 12 characters")
    private String password;

    @NotNull(message = "Please provide a valid <role>")
    private UserRole role;

    private String phone;

    // Nurse-specific
    private String licenseNumber;
    private String specialization;
    private Integer experienceYears;
    private String education;

    // Organization-specific
    private String orgName;
    private String orgType;
    private String regNumber;
    private String contactPerson;
    private String designation;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String website;
}
