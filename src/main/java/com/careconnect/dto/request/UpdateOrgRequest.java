package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrgRequest {

    @NotBlank(message = "Organization name is required")
    private String orgName;

    private String orgType;
    private String licenseNumber;
    private String contactPerson;
    private String designation;
    private String contact2FirstName;
    private String contact2MiddleName;
    private String contact2LastName;
    private String contact2Email;
    private String contact2Phone;
    private String contact2Designation;
    private String phone;
    private String addressLine1;
    private String addressLine2;
    private String landmark;
    private String country;
    private String city;
    private String state;
    private String pincode;
    private String website;
    private Integer bedCapacity;
    private String specializations;
    private String accreditation;
}
