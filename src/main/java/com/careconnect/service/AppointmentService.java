package com.careconnect.service;

import com.careconnect.dto.request.AppointmentApplicationRequest;
import com.careconnect.dto.request.AppointmentRequest;
import com.careconnect.dto.response.AppointmentApplicationResponse;
import com.careconnect.dto.response.AppointmentResponse;
import com.careconnect.entity.Appointment;
import com.careconnect.entity.AppointmentApplication;
import com.careconnect.entity.NurseProfile;
import com.careconnect.entity.PatientProfile;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.enums.AppointmentStatus;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.AppointmentApplicationRepository;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.NurseProfileRepository;
import com.careconnect.repository.PatientProfileRepository;
import com.careconnect.repository.ShiftRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final NurseProfileRepository nurseProfileRepository;
    private final AppointmentApplicationRepository apptApplicationRepository;
    private final NotificationService notificationService;
    private final ShiftRepository shiftRepository;

    @Transactional
    public AppointmentResponse bookAppointment(Long patientUserId, AppointmentRequest request) {
        PatientProfile patient = patientProfileRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", patientUserId));

        NurseProfile nurse = null;
        if (request.getNurseId() != null) {
            nurse = nurseProfileRepository.findById(request.getNurseId())
                    .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", request.getNurseId()));
        }

        Appointment appointment = Appointment.builder()
                .patient(patient)
                .nurse(nurse)
                .appointmentDate(request.getAppointmentDate())
                .careNeeds(request.getCareNeeds())
                .requiredSkills(request.getRequiredSkills())
                .duration(request.getDuration())
                .notes(request.getNotes())
                .bookingFor(request.getBookingFor())
                .patientFirstName(request.getPatientFirstName())
                .patientMiddleName(request.getPatientMiddleName())
                .patientLastName(request.getPatientLastName())
                .patientEmail(request.getPatientEmail())
                .patientPhone(request.getPatientPhone())
                .patientPhoneCountryCode(request.getPatientPhoneCountryCode())
                .patientAddressLine1(request.getPatientAddressLine1())
                .patientAddressLine2(request.getPatientAddressLine2())
                .patientLandmark(request.getPatientLandmark())
                .patientCity(request.getPatientCity())
                .patientState(request.getPatientState())
                .patientPincode(request.getPatientPincode())
                .scheduleType(request.getScheduleType())
                .scheduleDays(request.getScheduleDays())
                .priority(request.getPriority())
                .genderPreference(request.getGenderPreference())
                .languagePreference(request.getLanguagePreference())
                .specialization(request.getSpecialization())
                .medicalCondition(request.getMedicalCondition())
                .mobilityLevel(request.getMobilityLevel())
                .dietRequirements(request.getDietRequirements())
                .applicationDeadline(request.getApplicationDeadline())
                .build();

        appointmentRepository.save(appointment);
        log.info("Appointment booked for patient: {}", patient.getFullName());

        String care = appointment.getCareNeeds() != null ? appointment.getCareNeeds() : "Home Care";
        String loc  = appointment.getPatientCity() != null ? appointment.getPatientCity()
                    : appointment.getPatientState() != null ? appointment.getPatientState() : "India";
        notificationService.broadcast("NEW_REQUEST", care + "|" + loc);

        return toResponse(appointment);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByPatient(Long patientUserId) {
        PatientProfile patient = patientProfileRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", patientUserId));
        return appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(patient.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getByNurse(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return appointmentRepository.findByNurseId(nurse.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse reschedule(Long id, String newDate) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        appt.setAppointmentDate(LocalDateTime.parse(newDate.replace("Z", "").substring(0, 19)));
        appointmentRepository.save(appt);
        log.info("Appointment {} rescheduled to {}", id, newDate);
        return toResponse(appt);
    }

    @Transactional
    public AppointmentResponse updateStatus(Long id, AppointmentStatus status) {
        Appointment appt = appointmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", id));
        appt.setStatus(status);
        appointmentRepository.save(appt);
        return toResponse(appt);
    }

    @Transactional(readOnly = true)
    public List<AppointmentResponse> getOpenAppointments() {
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        return appointmentRepository.findOpenAppointments(AppointmentStatus.PENDING)
                .stream()
                .filter(a -> a.getApplicationDeadline() == null || a.getApplicationDeadline().isAfter(now))
                .map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AppointmentResponse assignNurse(Long appointmentId, Long nurseUserId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        appt.setNurse(nurse);
        appt.setStatus(AppointmentStatus.CONFIRMED);
        appointmentRepository.save(appt);
        log.info("Nurse {} assigned to appointment {}", nurse.getFullName(), appointmentId);
        return toResponse(appt);
    }

    // ── Nurse bids on a patient request ──────────────────────────────

    @Transactional
    public AppointmentApplicationResponse applyToAppointment(Long appointmentId, Long nurseUserId,
                                                              AppointmentApplicationRequest request) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));

        if (apptApplicationRepository.existsByNurseIdAndAppointmentId(nurse.getId(), appointmentId)) {
            throw new BadRequestException("You have already applied to this appointment request");
        }

        AppointmentApplication application = AppointmentApplication.builder()
                .appointment(appointment)
                .nurse(nurse)
                .salaryExpectation(request.getSalaryExpectation())
                .note(request.getNote())
                .build();

        apptApplicationRepository.save(application);
        log.info("Nurse {} applied to appointment {}", nurse.getFullName(), appointmentId);
        return toApplicationResponse(application);
    }

    @Transactional(readOnly = true)
    public List<AppointmentApplicationResponse> getAppointmentApplications(Long appointmentId) {
        return apptApplicationRepository.findByAppointmentId(appointmentId)
                .stream().map(this::toApplicationResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AppointmentApplicationResponse> getNurseAppointmentApplications(Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        return apptApplicationRepository.findByNurseId(nurse.getId())
                .stream().map(this::toApplicationResponse).collect(Collectors.toList());
    }

    @Transactional
    public void withdrawApplication(Long appointmentId, Long nurseUserId) {
        NurseProfile nurse = nurseProfileRepository.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));
        AppointmentApplication application = apptApplicationRepository
                .findByNurseIdAndAppointmentId(nurse.getId(), appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("AppointmentApplication", appointmentId));
        apptApplicationRepository.delete(application);
        log.info("Nurse {} withdrew application from appointment {}", nurse.getFullName(), appointmentId);
    }

    @Transactional
    public AppointmentResponse acceptApplication(Long applicationId) {
        AppointmentApplication chosen = apptApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("AppointmentApplication", applicationId));

        Appointment appointment = chosen.getAppointment();
        NurseProfile nurse = chosen.getNurse();

        // Mark this application APPROVED
        chosen.setStatus(ApplicationStatus.APPROVED);
        apptApplicationRepository.save(chosen);

        // Reject all other applications for this appointment
        apptApplicationRepository.findByAppointmentIdAndStatusNot(appointment.getId(), ApplicationStatus.APPROVED)
                .forEach(a -> {
                    a.setStatus(ApplicationStatus.REJECTED);
                    apptApplicationRepository.save(a);
                });

        // Assign nurse, confirm appointment, and store agreed rate per shift
        appointment.setNurse(nurse);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        if (chosen.getSalaryExpectation() != null) {
            appointment.setAgreedRatePerShift(
                java.math.BigDecimal.valueOf(chosen.getSalaryExpectation()));
        }
        appointmentRepository.save(appointment);

        log.info("Patient accepted nurse {} for appointment {}", nurse.getFullName(), appointment.getId());
        return toResponse(appointment);
    }

    private AppointmentResponse toResponse(Appointment a) {
        int applicantCount = apptApplicationRepository.countByAppointmentId(a.getId());
        NurseProfile nurse = a.getNurse();
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientUserId(a.getPatient().getUser() != null ? a.getPatient().getUser().getId() : null)
                .patientName(a.getPatient().getFullName())
                .nurseId(nurse != null ? nurse.getId() : null)
                .nurseUserId(nurse != null && nurse.getUser() != null ? nurse.getUser().getId() : null)
                .nurseName(nurse != null ? nurse.getFullName() : null)
                .nursePhone(nurse != null ? nurse.getPhone() : null)
                .nurseEmail(nurse != null && nurse.getUser() != null ? nurse.getUser().getEmail() : null)
                .nurseSpecialization(nurse != null ? nurse.getSpecialization() : null)
                .nurseExperience(nurse != null ? nurse.getExperienceYears() : null)
                .nurseEducation(nurse != null ? nurse.getEducation() : null)
                .nurseExpertise(nurse != null ? nurse.getExpertise() : null)
                .nurseLicenseNumber(nurse != null ? nurse.getLicenseNumber() : null)
                .nurseAvailability(nurse != null ? nurse.getAvailability() : null)
                .nurseRating(nurse != null ? nurse.getRating() : null)
                .appointmentDate(a.getAppointmentDate())
                .careNeeds(a.getCareNeeds())
                .requiredSkills(a.getRequiredSkills())
                .duration(a.getDuration())
                .status(a.getStatus())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .applicantCount(applicantCount)
                // Patient contact fields from booking form
                .bookingFor(a.getBookingFor())
                .patientFirstName(a.getPatientFirstName())
                .patientMiddleName(a.getPatientMiddleName())
                .patientLastName(a.getPatientLastName())
                .patientEmail(a.getPatientEmail())
                .patientPhone(a.getPatientPhone())
                .patientPhoneCountryCode(a.getPatientPhoneCountryCode())
                .patientAddressLine1(a.getPatientAddressLine1())
                .patientAddressLine2(a.getPatientAddressLine2())
                .patientLandmark(a.getPatientLandmark())
                .patientCity(a.getPatientCity())
                .patientState(a.getPatientState())
                .patientPincode(a.getPatientPincode())
                // Schedule & preferences
                .scheduleType(a.getScheduleType())
                .scheduleDays(a.getScheduleDays())
                .priority(a.getPriority())
                .genderPreference(a.getGenderPreference())
                .languagePreference(a.getLanguagePreference())
                .specialization(a.getSpecialization())
                .medicalCondition(a.getMedicalCondition())
                .mobilityLevel(a.getMobilityLevel())
                .dietRequirements(a.getDietRequirements())
                .applicationDeadline(a.getApplicationDeadline())
                .agreedRatePerShift(a.getAgreedRatePerShift())
                .endDate(a.getEndDate())
                .nurseUpiId(nurse != null ? nurse.getUpiId() : null)
                .nurseBankAccount(nurse != null ? nurse.getBankAccountNumber() : null)
                .nurseIfsc(nurse != null ? nurse.getBankIfscCode() : null)
                .nurseBankName(nurse != null ? nurse.getBankName() : null)
                .nursePreferredPaymentMode(nurse != null ? nurse.getPreferredPaymentMode() : null)
                .reconciliationStatus(a.getReconciliationStatus())
                .expectedShifts(a.getExpectedShifts())
                .actualShifts((int) shiftRepository.countNonRejectedByAppointmentId(a.getId()))
                .build();
    }

    private AppointmentApplicationResponse toApplicationResponse(AppointmentApplication a) {
        Appointment appt = a.getAppointment();
        NurseProfile nurse = a.getNurse();
        return AppointmentApplicationResponse.builder()
                .id(a.getId())
                .appointmentId(appt.getId())
                .patientName(appt.getPatient().getFullName())
                .careNeeds(appt.getCareNeeds())
                .requiredSkills(appt.getRequiredSkills())
                .notes(appt.getNotes())
                .appointmentDate(appt.getAppointmentDate())
                .nurseId(nurse.getId())
                .nurseName(nurse.getFullName())
                .nurseEmail(nurse.getUser() != null ? nurse.getUser().getEmail() : null)
                .nursePhone(nurse.getPhone())
                .nurseSpecialization(nurse.getSpecialization())
                .nurseExperience(nurse.getExperienceYears())
                .nurseEducation(nurse.getEducation())
                .nurseExpertise(nurse.getExpertise())
                .nurseLicenseNumber(nurse.getLicenseNumber())
                .nurseAvailability(nurse.getAvailability())
                .nursePreviousEmployment(nurse.getPreviousEmployment())
                .nurseReferences(nurse.getReferences())
                .nurseRating(nurse.getRating())
                .salaryExpectation(a.getSalaryExpectation())
                .note(a.getNote())
                .status(a.getStatus())
                .appliedAt(a.getAppliedAt())
                .build();
    }

    // ── Reconciliation ────────────────────────────────────────────────────────

    @Scheduled(fixedRate = 3_600_000) // runs every hour
    @Transactional
    public void triggerReconciliation() {
        List<Appointment> due = appointmentRepository.findAppointmentsNeedingReconciliation(LocalDateTime.now());
        for (Appointment appt : due) {
            int expected = calculateExpectedShifts(appt);
            int actual   = (int) shiftRepository.countNonRejectedByAppointmentId(appt.getId());

            appt.setExpectedShifts(expected);

            if (actual >= expected) {
                // All shifts done — auto-agree, no confirmation needed
                appt.setReconciliationStatus("AGREED");
            } else {
                appt.setReconciliationStatus("PENDING");
                // Notify both parties
                Long patientUserId = appt.getPatient().getUser().getId();
                Long nurseUserId   = appt.getNurse().getUser().getId();
                String msg = actual + " of " + expected + " expected shifts were marked for this appointment.";
                notificationService.pushToUser(patientUserId, "SHIFT",
                        "Shift Summary — Please Confirm", msg, appt.getId(), "APPOINTMENT");
                notificationService.pushToUser(nurseUserId, "SHIFT",
                        "Shift Summary — Please Confirm", msg, appt.getId(), "APPOINTMENT");
            }
            appointmentRepository.save(appt);
            log.info("Reconciliation triggered for appointment={} expected={} actual={} status={}",
                    appt.getId(), expected, actual, appt.getReconciliationStatus());
        }
    }

    @Transactional
    public AppointmentResponse reconcileByNurse(Long nurseUserId, Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        if (appt.getNurse() == null || !appt.getNurse().getUser().getId().equals(nurseUserId)) {
            throw new BadRequestException("This appointment does not belong to you.");
        }
        if (!"PENDING".equals(appt.getReconciliationStatus())
                && !"PATIENT_CONFIRMED".equals(appt.getReconciliationStatus())) {
            throw new BadRequestException("Reconciliation is not pending for this appointment.");
        }
        appt.setNurseReconciliationAt(LocalDateTime.now());
        String next = "PATIENT_CONFIRMED".equals(appt.getReconciliationStatus()) ? "AGREED" : "NURSE_CONFIRMED";
        appt.setReconciliationStatus(next);
        appointmentRepository.save(appt);
        log.info("Nurse {} confirmed reconciliation for appointment={}", nurseUserId, appointmentId);
        return toResponse(appt);
    }

    @Transactional
    public AppointmentResponse reconcileByPatient(Long patientUserId, Long appointmentId) {
        Appointment appt = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Appointment", appointmentId));
        if (!appt.getPatient().getUser().getId().equals(patientUserId)) {
            throw new BadRequestException("This appointment does not belong to you.");
        }
        if (!"PENDING".equals(appt.getReconciliationStatus())
                && !"NURSE_CONFIRMED".equals(appt.getReconciliationStatus())) {
            throw new BadRequestException("Reconciliation is not pending for this appointment.");
        }
        appt.setPatientReconciliationAt(LocalDateTime.now());
        String next = "NURSE_CONFIRMED".equals(appt.getReconciliationStatus()) ? "AGREED" : "PATIENT_CONFIRMED";
        appt.setReconciliationStatus(next);
        appointmentRepository.save(appt);
        log.info("Patient {} confirmed reconciliation for appointment={}", patientUserId, appointmentId);
        return toResponse(appt);
    }

    private int calculateExpectedShifts(Appointment appt) {
        LocalDate start = appt.getAppointmentDate().toLocalDate();
        LocalDate end   = appt.getEndDate() != null ? appt.getEndDate().toLocalDate() : start;
        String type     = appt.getScheduleType() != null ? appt.getScheduleType().toUpperCase() : "ONE_TIME";

        if ("ONE_TIME".equals(type)) return 1;

        if ("DAILY".equals(type)) {
            return (int) (end.toEpochDay() - start.toEpochDay() + 1);
        }

        if ("WEEKLY".equals(type)) {
            String days = appt.getScheduleDays();
            if (days == null || days.isBlank()) return 1;
            // Count occurrences of each selected weekday between start and end
            int count = 0;
            for (LocalDate d = start; !d.isAfter(end); d = d.plusDays(1)) {
                String fullName  = d.getDayOfWeek().name();          // MONDAY
                String shortName = fullName.substring(0, 3);          // MON
                String upperDays = days.toUpperCase();
                if (upperDays.contains(fullName) || upperDays.contains(shortName)) count++;
            }
            return count == 0 ? 1 : count;
        }

        if ("MONTHLY".equals(type)) {
            int months = (end.getYear() - start.getYear()) * 12
                       + (end.getMonthValue() - start.getMonthValue()) + 1;
            String days = appt.getScheduleDays();
            int daysPerMonth = (days != null && !days.isBlank()) ? days.split(",").length : 1;
            return months * daysPerMonth;
        }

        return 1;
    }

    public List<AppointmentResponse> fallbackList(Throwable t) {
        log.error("Circuit breaker: appointment service unavailable — {}", t.getMessage());
        return List.of();
    }
}
