package com.careconnect.service;

import com.careconnect.dto.request.LoginRequest;
import com.careconnect.dto.request.RegisterRequest;
import com.careconnect.dto.response.LoginResponse;
import com.careconnect.entity.*;
import com.careconnect.enums.UserRole;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.exception.UnauthorizedException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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
            String nFirst  = request.getFirstName()  != null ? request.getFirstName().trim()  : "";
            String nMiddle = request.getMiddleName() != null ? request.getMiddleName().trim()  : "";
            String nLast   = request.getLastName()   != null ? request.getLastName().trim()   : "";

            String nFullName = nFirst
                    + (nMiddle.isBlank() ? "" : " " + nMiddle)
                    + (nLast.isBlank()   ? "" : " " + nLast);
            if (nFullName.isBlank()) nFullName = request.getFullName() != null ? request.getFullName() : "";

            String nPhone = (request.getPhoneCountryCode() != null ? request.getPhoneCountryCode() : "+91")
                            + (request.getPhone() != null ? request.getPhone() : "");

            NurseProfile profile = NurseProfile.builder()
                    .user(user)
                    .fullName(nFullName.isBlank() ? user.getEmail() : nFullName)
                    .firstName(nFirst)
                    .middleName(nMiddle.isBlank() ? null : nMiddle)
                    .lastName(nLast)
                    .phone(nPhone)
                    .phoneCountryCode(request.getPhoneCountryCode())
                    .licenseNumber(request.getLicenseNumber())
                    .specialization(request.getSpecialization())
                    .experienceYears(request.getExperienceYears())
                    .education(request.getEducation())
                    .availability(request.getAvailability())
                    .addressLine1(request.getAddressLine1())
                    .addressLine2(request.getAddressLine2())
                    .landmark(request.getLandmark())
                    .country(request.getCountry() != null ? request.getCountry() : "India")
                    .state(request.getState())
                    .city(request.getCity())
                    .pincode(request.getPincode())
                    .build();
            nurseProfileRepository.save(profile);

        } else if (request.getRole() == UserRole.PATIENT) {
            String firstName  = request.getFirstName()  != null ? request.getFirstName().trim()  : "";
            String middleName = request.getMiddleName() != null ? request.getMiddleName().trim()  : "";
            String lastName   = request.getLastName()   != null ? request.getLastName().trim()   : "";

            String fullName = firstName
                    + (middleName.isBlank() ? "" : " " + middleName)
                    + (lastName.isBlank()   ? "" : " " + lastName);
            if (fullName.isBlank()) fullName = request.getFullName() != null ? request.getFullName() : "";

            String phone = (request.getPhoneCountryCode() != null ? request.getPhoneCountryCode() : "+91")
                           + (request.getPhone() != null ? request.getPhone() : "");

            PatientProfile profile = PatientProfile.builder()
                    .user(user)
                    .fullName(fullName.isBlank() ? user.getEmail() : fullName)
                    .firstName(firstName)
                    .middleName(middleName.isBlank() ? null : middleName)
                    .lastName(lastName)
                    .gender(request.getGender())
                    .bloodGroup(request.getBloodGroup())
                    .phone(phone)
                    .phoneCountryCode(request.getPhoneCountryCode())
                    .addressLine1(request.getAddressLine1())
                    .addressLine2(request.getAddressLine2())
                    .landmark(request.getLandmark())
                    .country(request.getCountry() != null ? request.getCountry() : "India")
                    .state(request.getState())
                    .city(request.getCity())
                    .pincode(request.getPincode())
                    .dateOfBirth(request.getDateOfBirth() != null && !request.getDateOfBirth().isBlank()
                            ? LocalDate.parse(request.getDateOfBirth()) : null)
                    .build();
            patientProfileRepository.save(profile);

        } else if (request.getRole() == UserRole.ORGANIZATION) {
            String cFirst  = request.getContactFirstName()  != null ? request.getContactFirstName().trim()  : "";
            String cMiddle = request.getContactMiddleName() != null ? request.getContactMiddleName().trim()  : "";
            String cLast   = request.getContactLastName()   != null ? request.getContactLastName().trim()   : "";
            String contactPerson = cFirst
                    + (cMiddle.isBlank() ? "" : " " + cMiddle)
                    + (cLast.isBlank()   ? "" : " " + cLast);
            if (contactPerson.isBlank()) contactPerson = request.getContactPerson() != null ? request.getContactPerson() : "";

            String orgPhone = (request.getPhoneCountryCode() != null ? request.getPhoneCountryCode() : "+91")
                            + (request.getPhone() != null ? request.getPhone() : "");

            Organization org = Organization.builder()
                    .user(user)
                    .orgName(request.getOrgName())
                    .orgType(request.getOrgType())
                    .regNumber(request.getRegNumber())
                    .licenseNumber(request.getOrgLicenseNumber())
                    .contactPerson(contactPerson)
                    .contactFirstName(cFirst.isBlank() ? null : cFirst)
                    .contactMiddleName(cMiddle.isBlank() ? null : cMiddle)
                    .contactLastName(cLast.isBlank() ? null : cLast)
                    .designation(request.getDesignation())
                    .phone(orgPhone)
                    .phoneCountryCode(request.getPhoneCountryCode())
                    .addressLine1(request.getAddressLine1())
                    .addressLine2(request.getAddressLine2())
                    .landmark(request.getLandmark())
                    .country(request.getCountry() != null ? request.getCountry() : "India")
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
        String identifier = request.getIdentifier();

        // Try email first; fall back to org registration number (direct User query — avoids lazy-load issue)
        User user = userRepository.findByEmail(identifier)
                .or(() -> organizationRepository.findUserByRegNumber(identifier))
                .or(() -> organizationRepository.findUserByLicenseNumber(identifier))
                .orElseThrow(() -> new UnauthorizedException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid credentials");
        }

        // Validate that the role selected on the login page matches the account's actual role
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                UserRole requestedRole = UserRole.valueOf(request.getRole().toUpperCase());
                if (user.getRole() != requestedRole) {
                    throw new UnauthorizedException("Invalid credentials");
                }
            } catch (IllegalArgumentException e) {
                throw new UnauthorizedException("Invalid credentials");
            }
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

    @Transactional
    public void changePassword(Long userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            throw new UnauthorizedException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", user.getEmail());
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
