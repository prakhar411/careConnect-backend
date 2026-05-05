package com.careconnect.service;

import com.careconnect.dto.request.AppointmentRequest;
import com.careconnect.dto.response.AppointmentResponse;
import com.careconnect.entity.Appointment;
import com.careconnect.entity.NurseProfile;
import com.careconnect.entity.PatientProfile;
import com.careconnect.enums.AppointmentStatus;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.AppointmentRepository;
import com.careconnect.repository.NurseProfileRepository;
import com.careconnect.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .build();

        appointmentRepository.save(appointment);
        log.info("Appointment booked for patient: {}", patient.getFullName());
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

    private AppointmentResponse toResponse(Appointment a) {
        return AppointmentResponse.builder()
                .id(a.getId())
                .patientId(a.getPatient().getId())
                .patientName(a.getPatient().getFullName())
                .nurseId(a.getNurse() != null ? a.getNurse().getId() : null)
                .nurseName(a.getNurse() != null ? a.getNurse().getFullName() : null)
                .appointmentDate(a.getAppointmentDate())
                .careNeeds(a.getCareNeeds())
                .duration(a.getDuration())
                .status(a.getStatus())
                .notes(a.getNotes())
                .createdAt(a.getCreatedAt())
                .build();
    }

    public List<AppointmentResponse> fallbackList(Throwable t) {
        log.error("Circuit breaker: appointment service unavailable — {}", t.getMessage());
        return List.of();
    }
}
