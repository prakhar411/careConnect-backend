package com.careconnect.repository;

import com.careconnect.entity.Appointment;
import com.careconnect.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByPatientId(Long patientId);
    List<Appointment> findByNurseId(Long nurseId);
    List<Appointment> findByStatus(AppointmentStatus status);
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(Long patientId);
    long countByStatus(AppointmentStatus status);
}
