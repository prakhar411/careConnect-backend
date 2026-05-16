package com.careconnect.repository;

import com.careconnect.entity.VitalSign;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VitalSignRepository extends JpaRepository<VitalSign, Long> {
    List<VitalSign> findByPatientUserIdOrderByRecordedAtDesc(Long patientUserId);
    List<VitalSign> findByAppointmentIdOrderByRecordedAtDesc(Long appointmentId);
}
