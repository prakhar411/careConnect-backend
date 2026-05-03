package com.careconnect.repository;

import com.careconnect.entity.NurseApplication;
import com.careconnect.enums.ApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NurseApplicationRepository extends JpaRepository<NurseApplication, Long> {
    List<NurseApplication> findByNurseId(Long nurseId);
    List<NurseApplication> findByJobId(Long jobId);
    List<NurseApplication> findByStatus(ApplicationStatus status);
    boolean existsByNurseIdAndJobId(Long nurseId, Long jobId);
    long countByStatus(ApplicationStatus status);

    @Query("SELECT a FROM NurseApplication a WHERE a.job.organization.user.id = :orgUserId")
    List<NurseApplication> findByOrgUserId(@Param("orgUserId") Long orgUserId);

    @Query("SELECT a FROM NurseApplication a WHERE a.job.organization.user.id = :orgUserId AND a.status = :status")
    List<NurseApplication> findByOrgUserIdAndStatus(@Param("orgUserId") Long orgUserId, @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(a) FROM NurseApplication a WHERE a.job.organization.user.id = :orgUserId AND a.status = :status")
    long countByOrgUserIdAndStatus(@Param("orgUserId") Long orgUserId, @Param("status") ApplicationStatus status);

    @Query("SELECT COUNT(DISTINCT a.nurse.id) FROM NurseApplication a WHERE a.job.organization.user.id = :orgUserId AND a.status = 'APPROVED'")
    long countHiredNursesByOrgUserId(@Param("orgUserId") Long orgUserId);
}
