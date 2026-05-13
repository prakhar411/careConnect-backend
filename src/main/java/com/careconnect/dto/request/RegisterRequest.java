package com.careconnect.dto.request;

import com.careconnect.enums.UserRole;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {

    // Optional — patients use firstName/lastName; nurse & org send fullName
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

    // Patient-specific
    private String firstName;
    private String middleName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String bloodGroup;
    private String phoneCountryCode;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;

    // Shared address fields (patient + org)
    private String city;
    private String state;
    private String pincode;

    // Nurse-specific
    private String licenseNumber;
    private String specialization;
    private Integer experienceYears;
    private String education;
    private String availability;

    // Organization-specific
    private String orgName;
    private String orgType;
    private String regNumber;
    private String orgLicenseNumber;
    private String contactPerson;
    private String contactFirstName;
    private String contactMiddleName;
    private String contactLastName;
    private String designation;
    private String address;
    private String website;
}
