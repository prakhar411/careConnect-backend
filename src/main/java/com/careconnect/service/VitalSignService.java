package com.careconnect.service;

import com.careconnect.dto.request.VitalSignRequest;
import com.careconnect.entity.VitalSign;
import com.careconnect.repository.VitalSignRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VitalSignService {

    private final VitalSignRepository repo;

    @Transactional
    public VitalSign save(VitalSignRequest req) {
        return repo.save(VitalSign.builder()
                .patientUserId(req.getPatientUserId())
                .nurseUserId(req.getNurseUserId())
                .appointmentId(req.getAppointmentId())
                .bloodPressure(req.getBloodPressure())
                .pulseRate(req.getPulseRate())
                .temperature(req.getTemperature())
                .spo2(req.getSpo2())
                .weight(req.getWeight())
                .notes(req.getNotes())
                .build());
    }

    @Transactional(readOnly = true)
    public List<VitalSign> getByPatient(Long patientUserId) {
        return repo.findByPatientUserIdOrderByRecordedAtDesc(patientUserId);
    }

    @Transactional(readOnly = true)
    public List<VitalSign> getByAppointment(Long appointmentId) {
        return repo.findByAppointmentIdOrderByRecordedAtDesc(appointmentId);
    }
}
