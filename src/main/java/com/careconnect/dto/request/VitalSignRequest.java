package com.careconnect.dto.request;

import lombok.Data;

@Data
public class VitalSignRequest {
    private Long patientUserId;
    private Long nurseUserId;
    private Long appointmentId;
    private String bloodPressure;
    private Integer pulseRate;
    private Double temperature;
    private Integer spo2;
    private Double weight;
    private String notes;
}
