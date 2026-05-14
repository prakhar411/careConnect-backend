package com.careconnect.repository;

import com.careconnect.entity.Appointment;
import com.careconnect.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.nurse WHERE a.patient.id = :patientId ORDER BY a.appointmentDate DESC")
    List<Appointment> findByPatientIdOrderByAppointmentDateDesc(@Param("patientId") Long patientId);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient LEFT JOIN FETCH a.nurse WHERE a.nurse.id = :nurseId ORDER BY a.appointmentDate DESC")
    List<Appointment> findByNurseId(@Param("nurseId") Long nurseId);

    @Query("SELECT a FROM Appointment a LEFT JOIN FETCH a.patient WHERE a.nurse IS NULL AND a.status = :status ORDER BY a.createdAt DESC")
    List<Appointment> findOpenAppointments(@Param("status") AppointmentStatus status);

    List<Appointment> findByStatus(AppointmentStatus status);
    long countByStatus(AppointmentStatus status);
}
