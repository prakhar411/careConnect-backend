package com.careconnect.service;

import com.careconnect.dto.request.ShiftRequest;
import com.careconnect.dto.response.ShiftResponse;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ShiftService {

    private final ShiftRepository       shiftRepository;
    private final AppointmentRepository appointmentRepository;
    private final NurseProfileRepository   nurseProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final PaymentRepository     paymentRepository;

    // ── Patient marks a shift done ────────────────────────────────────────────

    @Transactional
    public ShiftResponse markShift(Long patientUserId, ShiftRequest req) {
        Appointment appt = appointmentRepository.findById(req.getAppointmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", req.getAppointmentId()));

        if (!appt.getPatient().getUser().getId().equals(patientUserId)) {
            throw new BadRequestException("This appointment does not belong to you");
        }
        if (appt.getNurse() == null) {
            throw new BadRequestException("No nurse assigned to this appointment yet");
        }

        // Parse shiftDate string → LocalDate
        LocalDate parsedShiftDate;
        try {
            parsedShiftDate = LocalDate.parse(req.getShiftDate());
        } catch (Exception e) {
            throw new BadRequestException("Invalid shift date format. Use YYYY-MM-DD");
        }

        BigDecimal agreedRate = appt.getAgreedRatePerShift();
        BigDecimal negotiated = req.getNegotiatedRate();

        BigDecimal original;
        boolean isNegotiating;

        if (agreedRate != null) {
            // Bid-based appointment — rate auto-filled from nurse's salaryExpectation
            original      = agreedRate;
            isNegotiating = negotiated != null && negotiated.compareTo(original) != 0;
        } else {
            // Direct booking — no pre-agreed rate; patient must enter one via negotiatedRate field
            if (negotiated == null) {
                throw new BadRequestException("Please enter the rate for this shift");
            }
            original      = negotiated;
            isNegotiating = false;   // no pre-agreed rate to negotiate against
            negotiated    = null;
        }

        Shift shift = Shift.builder()
                .appointment(appt)
                .shiftDate(parsedShiftDate)
                .originalRate(original)
                .negotiatedRate(isNegotiating ? negotiated : null)
                .negotiationStatus(isNegotiating ? "PENDING" : "NONE")
                .notes(req.getNotes())
                .status("PENDING_CONFIRMATION")
                .build();

        shiftRepository.save(shift);
        log.info("Shift marked by patient={} for appointment={} date={} negotiating={}",
                patientUserId, req.getAppointmentId(), req.getShiftDate(), isNegotiating);
        return toResponse(shift);
    }

    // ── Nurse confirms shift — accepts negotiated rate ─────────────────────────

    @Transactional
    public ShiftResponse confirmShift(Long nurseUserId, Long shiftId, boolean acceptNegotiation) {
        Shift shift = getShiftAndValidateNurse(nurseUserId, shiftId);

        if (!"PENDING_CONFIRMATION".equals(shift.getStatus())) {
            throw new BadRequestException("Shift is already " + shift.getStatus());
        }

        // Resolve final rate
        BigDecimal finalRate;
        if ("PENDING".equals(shift.getNegotiationStatus())) {
            if (acceptNegotiation) {
                shift.setNegotiationStatus("ACCEPTED");
                finalRate = shift.getNegotiatedRate();
            } else {
                shift.setNegotiationStatus("REJECTED");
                finalRate = shift.getOriginalRate();
            }
        } else {
            finalRate = shift.getOriginalRate();
        }

        shift.setStatus("CONFIRMED");
        shift.setNurseConfirmedAt(LocalDateTime.now());
        shiftRepository.save(shift);

        // Auto-create PENDING payment for patient to pay
        Appointment appt         = shift.getAppointment();
        Long        patientUserId = appt.getPatient().getUser().getId();

        Payment payment = Payment.builder()
                .nurse(appt.getNurse())
                .appointment(appt)
                .amount(finalRate)
                .paymentStructure("SHIFT")
                .paidBy("PATIENT")
                .paidByUserId(patientUserId)
                .description("Shift on " + shift.getShiftDate()
                        + (shift.getNotes() != null ? " — " + shift.getNotes() : ""))
                .status(PaymentStatus.PENDING)
                .build();
        paymentRepository.save(payment);

        log.info("Shift {} confirmed by nurse={} finalRate={} negotiationAccepted={}",
                shiftId, nurseUserId, finalRate, acceptNegotiation);
        return toResponse(shift);
    }

    // ── Nurse rejects shift entirely ──────────────────────────────────────────

    @Transactional
    public ShiftResponse rejectShift(Long nurseUserId, Long shiftId) {
        Shift shift = getShiftAndValidateNurse(nurseUserId, shiftId);

        if (!"PENDING_CONFIRMATION".equals(shift.getStatus())) {
            throw new BadRequestException("Shift is already " + shift.getStatus());
        }

        shift.setStatus("REJECTED");
        shift.setNurseConfirmedAt(LocalDateTime.now());
        shiftRepository.save(shift);

        log.info("Shift {} rejected by nurse={}", shiftId, nurseUserId);
        return toResponse(shift);
    }

    // ── Queries ───────────────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShiftResponse> getByAppointment(Long appointmentId) {
        return shiftRepository.findByAppointmentIdOrderByShiftDateDesc(appointmentId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getByPatient(Long patientUserId) {
        return shiftRepository.findByPatientUserId(patientUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getByNurse(Long nurseUserId) {
        return shiftRepository.findByNurseUserId(nurseUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ShiftResponse> getPendingByNurse(Long nurseUserId) {
        return shiftRepository.findPendingByNurseUserId(nurseUserId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Shift getShiftAndValidateNurse(Long nurseUserId, Long shiftId) {
        Shift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", shiftId));
        if (shift.getAppointment().getNurse() == null ||
            !shift.getAppointment().getNurse().getUser().getId().equals(nurseUserId)) {
            throw new BadRequestException("This shift does not belong to your assignment");
        }
        return shift;
    }

    private BigDecimal computeFinalRate(Shift s) {
        if ("ACCEPTED".equals(s.getNegotiationStatus()) && s.getNegotiatedRate() != null) {
            return s.getNegotiatedRate();
        }
        return s.getOriginalRate();
    }

    private ShiftResponse toResponse(Shift s) {
        Appointment    appt    = s.getAppointment();
        PatientProfile patient = appt.getPatient();
        NurseProfile   nurse   = appt.getNurse();

        return ShiftResponse.builder()
                .id(s.getId())
                .appointmentId(appt.getId())
                .appointmentCareNeeds(appt.getCareNeeds())
                .patientName(patient != null ? patient.getFullName() : null)
                .patientUserId(patient != null && patient.getUser() != null ? patient.getUser().getId() : null)
                .nurseName(nurse != null ? nurse.getFullName() : null)
                .nurseUserId(nurse != null && nurse.getUser() != null ? nurse.getUser().getId() : null)
                .shiftDate(s.getShiftDate())
                .originalRate(s.getOriginalRate())
                .negotiatedRate(s.getNegotiatedRate())
                .negotiationStatus(s.getNegotiationStatus())
                .finalRate(computeFinalRate(s))
                .notes(s.getNotes())
                .status(s.getStatus())
                .patientMarkedAt(s.getPatientMarkedAt())
                .nurseConfirmedAt(s.getNurseConfirmedAt())
                .appointmentStartDate(appt.getAppointmentDate())
                .appointmentEndDate(appt.getEndDate())
                .build();
    }
}
