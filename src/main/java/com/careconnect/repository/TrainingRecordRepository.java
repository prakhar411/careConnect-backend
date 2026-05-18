package com.careconnect.repository;

import com.careconnect.entity.TrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {
    List<TrainingRecord> findByNurseUserIdOrderByCompletedAtDesc(Long nurseUserId);
    boolean existsByNurseUserIdAndCourseName(Long nurseUserId, String courseName);
}
