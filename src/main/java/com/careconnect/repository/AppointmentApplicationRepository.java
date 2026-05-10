package com.careconnect.repository;

import com.careconnect.entity.AppointmentApplication;
import com.careconnect.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentApplicationRepository extends JpaRepository<AppointmentApplication, Long> {
    List<AppointmentApplication> findByAppointmentId(Long appointmentId);
    List<AppointmentApplication> findByNurseId(Long nurseId);
    boolean existsByNurseIdAndAppointmentId(Long nurseId, Long appointmentId);
    int countByAppointmentId(Long appointmentId);
    List<AppointmentApplication> findByAppointmentIdAndStatusNot(Long appointmentId, ApplicationStatus status);
    java.util.Optional<AppointmentApplication> findByNurseIdAndAppointmentId(Long nurseId, Long appointmentId);
}
