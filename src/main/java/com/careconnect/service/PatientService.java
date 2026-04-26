package com.careconnect.service;

import com.careconnect.entity.PatientProfile;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.PatientProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientService {

    private final PatientProfileRepository patientProfileRepository;

    public PatientProfile getProfile(Long userId) {
        return patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", userId));
    }

    @Transactional
    public PatientProfile updateProfile(Long userId, Map<String, Object> updates) {
        PatientProfile profile = patientProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("PatientProfile", userId));

        updates.forEach((key, value) -> {
            switch (key) {
                case "fullName" -> profile.setFullName((String) value);
                case "phone" -> profile.setPhone((String) value);
                case "address" -> profile.setAddress((String) value);
                case "city" -> profile.setCity((String) value);
                case "state" -> profile.setState((String) value);
                case "bloodGroup" -> profile.setBloodGroup((String) value);
                case "emergencyContact" -> profile.setEmergencyContact((String) value);
                case "emergencyContactPhone" -> profile.setEmergencyContactPhone((String) value);
                case "medicalHistory" -> profile.setMedicalHistory((String) value);
                case "allergies" -> profile.setAllergies((String) value);
                case "currentMedications" -> profile.setCurrentMedications((String) value);
            }
        });

        return patientProfileRepository.save(profile);
    }
}
