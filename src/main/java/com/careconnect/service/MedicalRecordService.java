package com.careconnect.service;

import com.careconnect.dto.response.AppointmentResponse;
import com.careconnect.entity.MedicalRecord;
import com.careconnect.entity.PatientProfile;
import com.careconnect.entity.User;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.MedicalRecordRepository;
import com.careconnect.repository.PatientProfileRepository;
import com.careconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class MedicalRecordService {

    private final MedicalRecordRepository medicalRecordRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final UserRepository userRepository;

    @Transactional
    public MedicalRecord addRecord(Long patientUserId, Long uploaderUserId,
                                    String recordType, String title, String description, String fileUrl) {
        PatientProfile patient = patientProfileRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", patientUserId));
        User uploader = userRepository.findById(uploaderUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", uploaderUserId));

        MedicalRecord record = MedicalRecord.builder()
                .patient(patient)
                .uploadedBy(uploader)
                .recordType(recordType)
                .title(title)
                .description(description)
                .fileUrl(fileUrl)
                .build();

        return medicalRecordRepository.save(record);
    }

    @Transactional(readOnly = true)
    public List<MedicalRecord> getByPatient(Long patientUserId) {
        PatientProfile patient = patientProfileRepository.findByUserId(patientUserId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", patientUserId));
        return medicalRecordRepository.findByPatientIdOrderByCreatedAtDesc(patient.getId());
    }

    @Transactional
    public void delete(Long id) {
        medicalRecordRepository.deleteById(id);
    }
}
