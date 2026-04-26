package com.careconnect.service;

import com.careconnect.dto.request.LoginRequest;
import com.careconnect.dto.request.RegisterRequest;
import com.careconnect.dto.response.LoginResponse;
import com.careconnect.entity.*;
import com.careconnect.enums.UserRole;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.UnauthorizedException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final NurseProfileRepository nurseProfileRepository;
    private final PatientProfileRepository patientProfileRepository;
    private final OrganizationRepository organizationRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole())
                .build();
        userRepository.save(user);

        if (request.getRole() == UserRole.NURSE) {
            NurseProfile profile = NurseProfile.builder()
                    .user(user)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .licenseNumber(request.getLicenseNumber())
                    .specialization(request.getSpecialization())
                    .experienceYears(request.getExperienceYears())
                    .education(request.getEducation())
                    .build();
            nurseProfileRepository.save(profile);

        } else if (request.getRole() == UserRole.PATIENT) {
            PatientProfile profile = PatientProfile.builder()
                    .user(user)
                    .fullName(request.getFullName())
                    .phone(request.getPhone())
                    .build();
            patientProfileRepository.save(profile);

        } else if (request.getRole() == UserRole.ORGANIZATION) {
            Organization org = Organization.builder()
                    .user(user)
                    .orgName(request.getOrgName())
                    .orgType(request.getOrgType())
                    .regNumber(request.getRegNumber())
                    .contactPerson(request.getContactPerson())
                    .designation(request.getDesignation())
                    .phone(request.getPhone())
                    .address(request.getAddress())
                    .city(request.getCity())
                    .state(request.getState())
                    .pincode(request.getPincode())
                    .website(request.getWebsite())
                    .build();
            organizationRepository.save(org);
        }

        log.info("User registered: {} role={}", request.getEmail(), request.getRole());
        return "User registered successfully";
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getIdentifier())
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Temporary placeholder token (JWT will be re-enabled with security)
        String token = Base64.getEncoder()
                .encodeToString((user.getEmail() + ":" + user.getRole()).getBytes());

        String fullName = resolveFullName(user);
        log.info("User logged in: {}", user.getEmail());

        return LoginResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .userId(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                .fullName(fullName)
                .build();
    }

    private String resolveFullName(User user) {
        return switch (user.getRole()) {
            case NURSE -> nurseProfileRepository.findByUserId(user.getId())
                    .map(NurseProfile::getFullName).orElse(user.getEmail());
            case PATIENT -> patientProfileRepository.findByUserId(user.getId())
                    .map(PatientProfile::getFullName).orElse(user.getEmail());
            case ORGANIZATION -> organizationRepository.findByUserId(user.getId())
                    .map(Organization::getOrgName).orElse(user.getEmail());
        };
    }
}
