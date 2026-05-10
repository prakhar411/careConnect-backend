package com.careconnect.dto.request;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class AppointmentApplicationRequest {
    private Double salaryExpectation;
    private String note;
}
