package com.careconnect.service;

import com.careconnect.dto.response.NurseResponse;
import com.careconnect.entity.NurseProfile;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.NurseProfileRepository;
import com.careconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NurseService {

    private final NurseProfileRepository nurseProfileRepository;
    private final UserRepository userRepository;

    public NurseResponse getProfile(Long userId) {
        NurseProfile profile = nurseProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", userId));
        return toResponse(profile);
    }

    @Transactional
    public NurseResponse updateProfile(Long userId, Map<String, Object> updates) {
        NurseProfile profile = nurseProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", userId));

        updates.forEach((key, value) -> {
            switch (key) {
                case "fullName" -> profile.setFullName((String) value);
                case "phone" -> profile.setPhone((String) value);
                case "specialization" -> profile.setSpecialization((String) value);
                case "expertise" -> profile.setExpertise((String) value);
                case "education" -> profile.setEducation((String) value);
                case "availability" -> profile.setAvailability((String) value);
                case "previousEmployment" -> profile.setPreviousEmployment((String) value);
                case "references" -> profile.setReferences((String) value);
            }
        });

        nurseProfileRepository.save(profile);
        return toResponse(profile);
    }

    public List<NurseResponse> searchNurses(String specialization, String availability) {
        return nurseProfileRepository.searchNurses(specialization, availability)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public List<NurseResponse> getAllNurses() {
        return nurseProfileRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NurseResponse toResponse(NurseProfile profile) {
        return NurseResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .email(profile.getUser().getEmail())
                .licenseNumber(profile.getLicenseNumber())
                .phone(profile.getPhone())
                .specialization(profile.getSpecialization())
                .education(profile.getEducation())
                .expertise(profile.getExpertise())
                .experienceYears(profile.getExperienceYears())
                .availability(profile.getAvailability())
                .rating(profile.getRating())
                .profileStatus(profile.getProfileStatus())
                .build();
    }

    public NurseResponse fallbackNurse(Long userId, Throwable t) {
        log.error("Circuit breaker: nurse service unavailable — {}", t.getMessage());
        return NurseResponse.builder().build();
    }

    public List<NurseResponse> fallbackList(String s, String a, Throwable t) {
        log.error("Circuit breaker: nurse search unavailable — {}", t.getMessage());
        return List.of();
    }
}
