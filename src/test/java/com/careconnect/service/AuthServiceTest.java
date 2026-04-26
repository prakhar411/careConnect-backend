package com.careconnect.service;

import com.careconnect.dto.request.RegisterRequest;
import com.careconnect.entity.User;
import com.careconnect.enums.UserRole;
import com.careconnect.exception.BadRequestException;
import com.careconnect.repository.*;
import com.careconnect.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserRepository userRepository;
    @Mock NurseProfileRepository nurseProfileRepository;
    @Mock PatientProfileRepository patientProfileRepository;
    @Mock OrganizationRepository organizationRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtTokenProvider jwtTokenProvider;
    @Mock AuthenticationManager authenticationManager;

    @InjectMocks AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFullName("Jane Doe");
        registerRequest.setEmail("jane@test.com");
        registerRequest.setPassword("password123");
        registerRequest.setRole(UserRole.PATIENT);
    }

    @Test
    void register_shouldSucceed_whenEmailNotTaken() {
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(new User());
        when(patientProfileRepository.save(any())).thenReturn(null);

        String result = authService.register(registerRequest);

        assertEquals("User registered successfully", result);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_shouldThrow_whenEmailAlreadyExists() {
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_shouldSaveNurseProfile_whenRoleIsNurse() {
        registerRequest.setRole(UserRole.NURSE);
        registerRequest.setLicenseNumber("LIC123");

        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(User.builder().id(1L).build());
        when(nurseProfileRepository.save(any())).thenReturn(null);

        authService.register(registerRequest);

        verify(nurseProfileRepository).save(any());
        verify(patientProfileRepository, never()).save(any());
    }
}
