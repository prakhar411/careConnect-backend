package com.careconnect.repository;

import com.careconnect.entity.Shift;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, Long> {

    // All shifts for one appointment (newest first)
    List<Shift> findByAppointmentIdOrderByShiftDateDesc(Long appointmentId);

    // All shifts for a patient (across all appointments)
    @Query("SELECT s FROM Shift s WHERE s.appointment.patient.user.id = :patientUserId ORDER BY s.shiftDate DESC")
    List<Shift> findByPatientUserId(@Param("patientUserId") Long patientUserId);

    // All shifts for a nurse (across all appointments)
    @Query("SELECT s FROM Shift s WHERE s.appointment.nurse.user.id = :nurseUserId ORDER BY s.shiftDate DESC")
    List<Shift> findByNurseUserId(@Param("nurseUserId") Long nurseUserId);

    // Pending confirmation shifts for a nurse (to show badge count)
    @Query("SELECT s FROM Shift s WHERE s.appointment.nurse.user.id = :nurseUserId AND s.status = 'PENDING_CONFIRMATION'")
    List<Shift> findPendingByNurseUserId(@Param("nurseUserId") Long nurseUserId);

    // Count confirmed shifts for an appointment
    @Query("SELECT COUNT(s) FROM Shift s WHERE s.appointment.id = :appointmentId AND s.status = 'CONFIRMED'")
    long countConfirmedByAppointmentId(@Param("appointmentId") Long appointmentId);
}
