package com.careconnect.repository;

import com.careconnect.entity.CareGoal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareGoalRepository extends JpaRepository<CareGoal, Long> {
    List<CareGoal> findByPatientUserIdOrderByCreatedAtDesc(Long patientUserId);
}
