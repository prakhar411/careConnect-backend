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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock AppointmentRepository appointmentRepository;
    @Mock PatientProfileRepository patientProfileRepository;
    @Mock NurseProfileRepository nurseProfileRepository;

    @InjectMocks AppointmentService appointmentService;

    private PatientProfile patient;
    private AppointmentRequest request;

    @BeforeEach
    void setUp() {
        patient = PatientProfile.builder().id(1L).fullName("John Patient").build();

        request = new AppointmentRequest();
        request.setAppointmentDate(LocalDateTime.now().plusDays(2));
        request.setCareNeeds("Post-surgery wound care");
        request.setDuration("2 hours");
    }

    @Test
    void bookAppointment_shouldSucceed() {
        when(patientProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(patient));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> {
            Appointment a = inv.getArgument(0);
            a.setId(1L);
            return a;
        });

        AppointmentResponse response = appointmentService.bookAppointment(1L, request);

        assertNotNull(response);
        assertEquals(AppointmentStatus.PENDING, response.getStatus());
        assertEquals("Post-surgery wound care", response.getCareNeeds());
    }

    @Test
    void bookAppointment_shouldThrow_whenPatientNotFound() {
        when(patientProfileRepository.findByUserId(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> appointmentService.bookAppointment(99L, request));
    }

    @Test
    void getByPatient_shouldReturnList() {
        Appointment appointment = Appointment.builder()
                .id(1L).patient(patient).status(AppointmentStatus.PENDING).build();

        when(patientProfileRepository.findByUserId(anyLong())).thenReturn(Optional.of(patient));
        when(appointmentRepository.findByPatientIdOrderByAppointmentDateDesc(anyLong()))
                .thenReturn(List.of(appointment));

        List<AppointmentResponse> result = appointmentService.getByPatient(1L);

        assertEquals(1, result.size());
    }

    @Test
    void updateStatus_shouldChangeStatus() {
        Appointment appointment = Appointment.builder()
                .id(1L).patient(patient).status(AppointmentStatus.PENDING).build();

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(appointment));
        when(appointmentRepository.save(any())).thenReturn(appointment);

        AppointmentResponse response = appointmentService.updateStatus(1L, AppointmentStatus.CONFIRMED);

        assertEquals(AppointmentStatus.CONFIRMED, response.getStatus());
    }
}
