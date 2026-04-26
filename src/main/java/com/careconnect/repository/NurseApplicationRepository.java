package com.careconnect.repository;

import com.careconnect.entity.NurseApplication;
import com.careconnect.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NurseApplicationRepository extends JpaRepository<NurseApplication, Long> {
    List<NurseApplication> findByNurseId(Long nurseId);
    List<NurseApplication> findByJobId(Long jobId);
    List<NurseApplication> findByStatus(ApplicationStatus status);
    boolean existsByNurseIdAndJobId(Long nurseId, Long jobId);
    long countByStatus(ApplicationStatus status);
}
