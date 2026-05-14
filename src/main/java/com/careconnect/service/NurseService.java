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

    @Transactional(readOnly = true)
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
                case "fullName"          -> profile.setFullName((String) value);
                case "firstName"         -> profile.setFirstName((String) value);
                case "middleName"        -> profile.setMiddleName((String) value);
                case "lastName"          -> profile.setLastName((String) value);
                case "phone"             -> profile.setPhone((String) value);
                case "phoneCountryCode"  -> profile.setPhoneCountryCode((String) value);
                case "specialization"    -> profile.setSpecialization((String) value);
                case "expertise"         -> profile.setExpertise((String) value);
                case "education"         -> profile.setEducation((String) value);
                case "availability"      -> profile.setAvailability((String) value);
                case "address"           -> profile.setAddress((String) value);
                case "addressLine1"      -> profile.setAddressLine1((String) value);
                case "addressLine2"      -> profile.setAddressLine2((String) value);
                case "landmark"          -> profile.setLandmark((String) value);
                case "country"           -> profile.setCountry((String) value);
                case "state"             -> profile.setState((String) value);
                case "city"              -> profile.setCity((String) value);
                case "pincode"           -> profile.setPincode((String) value);
                case "previousEmployment"-> profile.setPreviousEmployment((String) value);
                case "references"        -> profile.setReferences((String) value);
                case "experienceYears"   -> {
                    if (value instanceof Number n) profile.setExperienceYears(n.intValue());
                    else if (value instanceof String s && !s.isBlank())
                        profile.setExperienceYears(Integer.parseInt(s));
                }
                case "availableForEmergency" -> {
                    if (value instanceof Boolean b) profile.setAvailableForEmergency(b);
                    else if (value instanceof String s) profile.setAvailableForEmergency(Boolean.parseBoolean(s));
                }
            }
        });

        nurseProfileRepository.save(profile);
        return toResponse(profile);
    }

    @Transactional(readOnly = true)
    public List<NurseResponse> searchNurses(String specialization, String availability) {
        return nurseProfileRepository.searchNurses(specialization, availability)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<NurseResponse> getAllNurses() {
        return nurseProfileRepository.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    private NurseResponse toResponse(NurseProfile profile) {
        return NurseResponse.builder()
                .id(profile.getId())
                .userId(profile.getUser().getId())
                .fullName(profile.getFullName())
                .firstName(profile.getFirstName())
                .middleName(profile.getMiddleName())
                .lastName(profile.getLastName())
                .email(profile.getUser().getEmail())
                .licenseNumber(profile.getLicenseNumber())
                .phone(profile.getPhone())
                .phoneCountryCode(profile.getPhoneCountryCode())
                .specialization(profile.getSpecialization())
                .education(profile.getEducation())
                .expertise(profile.getExpertise())
                .experienceYears(profile.getExperienceYears())
                .availability(profile.getAvailability())
                .address(profile.getAddress())
                .addressLine1(profile.getAddressLine1())
                .addressLine2(profile.getAddressLine2())
                .landmark(profile.getLandmark())
                .country(profile.getCountry())
                .state(profile.getState())
                .city(profile.getCity())
                .pincode(profile.getPincode())
                .references(profile.getReferences())
                .rating(profile.getRating())
                .profileStatus(profile.getProfileStatus())
                .availableForEmergency(profile.getAvailableForEmergency())
                .upiId(profile.getUpiId())
                .bankAccountNumber(profile.getBankAccountNumber())
                .bankIfscCode(profile.getBankIfscCode())
                .bankName(profile.getBankName())
                .preferredPaymentMode(profile.getPreferredPaymentMode())
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
