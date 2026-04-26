package com.careconnect.service;

import com.careconnect.dto.request.PaymentRequest;
import com.careconnect.dto.response.PaymentResponse;
import com.careconnect.entity.Appointment;
import com.careconnect.entity.NurseProfile;
import com.careconnect.entity.Payment;
import com.careconnect.enums.PaymentStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.NurseProfileRepository;
import com.careconnect.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NurseProfileRepository nurseProfileRepository;
    private final AppointmentRepository appointmentRepository;

    @Transactional
    public PaymentResponse createPayment(Long nurseUserId, PaymentRequest request) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));

        Appointment appointment = null;
        if (request.getAppointmentId() != null) {
            appointment = appointmentRepository.findById(request.getAppointmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Appointment", request.getAppointmentId()));
        }

        Payment payment = Payment.builder()
                .nurse(nurse)
                .appointment(appointment)
                .amount(request.getAmount())
                .paymentMethod(request.getPaymentMethod())
                .paymentStructure(request.getPaymentStructure())
                .hoursWorked(request.getHoursWorked())
                .description(request.getDescription())
                .build();

        paymentRepository.save(payment);
        return toResponse(payment);
    }

    public List<PaymentResponse> getByNurse(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return paymentRepository.findByNurseIdOrderByPaymentDateDesc(nurse.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public BigDecimal getTotalEarnings(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return paymentRepository.sumProcessedByNurseId(nurse.getId());
    }

    @Transactional
    public PaymentResponse updateStatus(Long id, PaymentStatus status) {
        Payment payment = paymentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Payment", id));
        payment.setStatus(status);
        paymentRepository.save(payment);
        return toResponse(payment);
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .nurseId(p.getNurse().getId())
                .nurseName(p.getNurse().getFullName())
                .appointmentId(p.getAppointment() != null ? p.getAppointment().getId() : null)
                .amount(p.getAmount())
                .paymentMethod(p.getPaymentMethod())
                .paymentStructure(p.getPaymentStructure())
                .hoursWorked(p.getHoursWorked())
                .taxDeduction(p.getTaxDeduction())
                .description(p.getDescription())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .build();
    }
}
