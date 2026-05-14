package com.careconnect.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class NurseBankDetailsRequest {

    private String upiId;
    private String bankAccountNumber;
    private String bankIfscCode;
    private String bankName;

    @NotBlank(message = "Please select a preferred payment mode")
    private String preferredPaymentMode; // UPI or BANK_TRANSFER
}
