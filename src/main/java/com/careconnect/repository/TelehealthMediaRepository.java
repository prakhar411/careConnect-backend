package com.careconnect.repository;

import com.careconnect.entity.TelehealthMedia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TelehealthMediaRepository extends JpaRepository<TelehealthMedia, Long> {
    List<TelehealthMedia> findByNurseUserIdOrderByCreatedAtDesc(Long nurseUserId);
    List<TelehealthMedia> findByPatientUserIdOrderByCreatedAtDesc(Long patientUserId);
}
