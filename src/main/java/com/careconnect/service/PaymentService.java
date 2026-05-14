package com.careconnect.service;

import com.careconnect.dto.request.MonthlySalaryRequest;
import com.careconnect.dto.request.NurseBankDetailsRequest;
import com.careconnect.dto.request.ProcessPaymentRequest;
import com.careconnect.dto.request.ShiftPaymentRequest;
import com.careconnect.dto.response.PaymentResponse;
import com.careconnect.entity.*;
import com.careconnect.enums.PaymentStatus;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final NurseProfileRepository nurseProfileRepository;
    private final AppointmentRepository appointmentRepository;
    private final OrganizationRepository organizationRepository;

    // ── Existing ──────────────────────────────────────────────────────────────

    @Transactional
    public PaymentResponse createPayment(Long nurseUserId, com.careconnect.dto.request.PaymentRequest request) {
        NurseProfile nurse = findNurseByUserId(nurseUserId);
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

    @Transactional(readOnly = true)
    public List<PaymentResponse> getByNurse(Long nurseUserId) {
        NurseProfile nurse = findNurseByUserId(nurseUserId);
        return paymentRepository.findByNurseIdOrderByPaymentDateDesc(nurse.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalEarnings(Long nurseUserId) {
        NurseProfile nurse = findNurseByUserId(nurseUserId);
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

    // ── Bank Details ──────────────────────────────────────────────────────────

    @Transactional
    public void saveBankDetails(Long nurseUserId, NurseBankDetailsRequest req) {
        NurseProfile nurse = findNurseByUserId(nurseUserId);
        nurse.setUpiId(req.getUpiId());
        nurse.setBankAccountNumber(req.getBankAccountNumber());
        nurse.setBankIfscCode(req.getBankIfscCode());
        nurse.setBankName(req.getBankName());
        nurse.setPreferredPaymentMode(req.getPreferredPaymentMode());
        nurseProfileRepository.save(nurse);
        log.info("Bank details saved for nurse userId={}", nurseUserId);
    }

    // ── Shift Payment (Patient → Nurse) ───────────────────────────────────────

    @Transactional
    public PaymentResponse markShiftComplete(Long nurseUserId, ShiftPaymentRequest req) {
        NurseProfile nurse = findNurseByUserId(nurseUserId);
        Appointment appt = appointmentRepository.findById(req.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", req.getAppointmentId()));

        // Ensure this nurse is actually assigned to this appointment
        if (appt.getNurse() == null || !appt.getNurse().getId().equals(nurse.getId())) {
            throw new BadRequestException("You are not assigned to this appointment");
        }

        Long patientUserId = appt.getPatient().getUser().getId();

        Payment payment = Payment.builder()
                .nurse(nurse)
                .appointment(appt)
                .amount(req.getRatePerShift())
                .paymentStructure("SHIFT")
                .paidBy("PATIENT")
                .paidByUserId(patientUserId)
                .description(req.getNotes() != null ? req.getNotes() : "Shift completed")
                .status(PaymentStatus.PENDING)
                .build();

        paymentRepository.save(payment);
        log.info("Shift marked complete: nurse={} appointment={} amount={}", nurseUserId, req.getAppointmentId(), req.getRatePerShift());
        return toResponse(payment);
    }

    // Patient sees all pending shift payments they owe
    @Transactional(readOnly = true)
    public List<PaymentResponse> getPendingShiftsByPatient(Long patientUserId) {
        return paymentRepository.findPendingShiftsByPatientUserId(patientUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // Patient pays all pending shifts for one appointment (simulation)
    @Transactional
    public List<PaymentResponse> processPatientPayment(Long patientUserId, ProcessPaymentRequest req) {
        List<Payment> pending = paymentRepository.findPendingShiftsByAppointment(req.getAppointmentId(), patientUserId);
        if (pending.isEmpty()) {
            throw new BadRequestException("No pending payments found for this appointment");
        }
        pending.forEach(p -> {
            p.setStatus(PaymentStatus.PROCESSED);
            p.setPaymentMethod(req.getPaymentMethod());
        });
        paymentRepository.saveAll(pending);
        log.info("Patient {} processed {} shift payment(s) for appointment {}", patientUserId, pending.size(), req.getAppointmentId());
        return pending.stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Monthly Salary (Org → Nurse) ──────────────────────────────────────────

    @Transactional
    public PaymentResponse processMonthlySalary(Long orgUserId, MonthlySalaryRequest req) {
        NurseProfile nurse = findNurseByUserId(req.getNurseUserId());
        Organization org = organizationRepository.findByUserId(orgUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgUserId));

        BigDecimal base  = req.getBaseSalary();
        BigDecimal hra   = orZero(req.getHra());
        BigDecimal ta    = orZero(req.getTravelAllowance());
        BigDecimal other = orZero(req.getOtherAllowances());
        BigDecimal gross = base.add(hra).add(ta).add(other);

        // Deductions: TDS 10% on gross, PF 12% on base, ESI 0.75% on gross
        BigDecimal tds = gross.multiply(BigDecimal.valueOf(0.10)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal pf  = base.multiply(BigDecimal.valueOf(0.12)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal esi = gross.multiply(BigDecimal.valueOf(0.0075)).setScale(0, RoundingMode.HALF_UP);
        BigDecimal netPay = gross.subtract(tds).subtract(pf).subtract(esi);

        Payment payment = Payment.builder()
                .nurse(nurse)
                .amount(netPay)
                .grossAmount(gross)
                .taxDeduction(tds)
                .pfDeduction(pf)
                .esiDeduction(esi)
                .paymentMethod("BANK_TRANSFER")
                .paymentStructure("MONTHLY_SALARY")
                .paidBy("ORGANIZATION")
                .paidByUserId(orgUserId)
                .salaryMonth(req.getSalaryMonth())
                .description("HRA=" + hra + "|TA=" + ta + "|Others=" + other + "|Org=" + org.getOrgName())
                .status(PaymentStatus.PROCESSED)
                .build();

        paymentRepository.save(payment);
        log.info("Monthly salary processed: org={} nurse={} month={} net={}", orgUserId, req.getNurseUserId(), req.getSalaryMonth(), netPay);
        return toResponse(payment);
    }

    // Org sees salary payments they've made
    @Transactional(readOnly = true)
    public List<PaymentResponse> getOrgSalaryHistory(Long orgUserId) {
        return paymentRepository.findSalaryPaymentsByOrgUserId(orgUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private NurseProfile findNurseByUserId(Long userId) {
        return nurseProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", userId));
    }

    private BigDecimal orZero(BigDecimal val) {
        return val != null ? val : BigDecimal.ZERO;
    }

    private PaymentResponse toResponse(Payment p) {
        NurseProfile nurse = p.getNurse();
        String patientName = null;
        if (p.getAppointment() != null && p.getAppointment().getPatient() != null) {
            patientName = p.getAppointment().getPatient().getFullName();
        }

        // Parse org name from description for salary payments
        String orgName = null;
        if ("MONTHLY_SALARY".equals(p.getPaymentStructure()) && p.getDescription() != null) {
            for (String part : p.getDescription().split("\\|")) {
                if (part.startsWith("Org=")) { orgName = part.substring(4); break; }
            }
        }

        return PaymentResponse.builder()
                .id(p.getId())
                .nurseId(nurse.getId())
                .nurseName(nurse.getFullName())
                .appointmentId(p.getAppointment() != null ? p.getAppointment().getId() : null)
                .paidBy(p.getPaidBy())
                .paidByUserId(p.getPaidByUserId())
                .amount(p.getAmount())
                .grossAmount(p.getGrossAmount())
                .taxDeduction(p.getTaxDeduction())
                .pfDeduction(p.getPfDeduction())
                .esiDeduction(p.getEsiDeduction())
                .paymentMethod(p.getPaymentMethod())
                .paymentStructure(p.getPaymentStructure())
                .hoursWorked(p.getHoursWorked())
                .salaryMonth(p.getSalaryMonth())
                .description(p.getDescription())
                .status(p.getStatus())
                .paymentDate(p.getPaymentDate())
                .nurseUpiId(nurse.getUpiId())
                .nurseBankAccount(nurse.getBankAccountNumber())
                .nurseIfsc(nurse.getBankIfscCode())
                .nurseBankName(nurse.getBankName())
                .nursePreferredPaymentMode(nurse.getPreferredPaymentMode())
                .patientName(patientName)
                .orgName(orgName)
                .appointmentCareNeeds(p.getAppointment() != null ? p.getAppointment().getCareNeeds() : null)
                .build();
    }

    public List<PaymentResponse> fallbackList(Throwable t) {
        log.error("Circuit breaker: payment service unavailable — {}", t.getMessage());
        return List.of();
    }
}
