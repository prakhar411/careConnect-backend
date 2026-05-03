package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateOrgRequest {

    @NotBlank(message = "Organization name is required")
    private String orgName;

    private String orgType;
    private String contactPerson;
    private String designation;
    private String phone;
    private String address;
    private String city;
    private String state;
    private String pincode;
    private String website;
    private Integer bedCapacity;
    private String specializations;
    private String accreditation;
}
