package com.careconnect.repository;

import com.careconnect.entity.Job;
import com.careconnect.enums.JobStatus;
import com.careconnect.enums.JobType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    @Query("SELECT j FROM Job j WHERE j.organization.id = :organizationId ORDER BY j.createdAt DESC")
    List<Job> findByOrganizationId(@Param("organizationId") Long organizationId);
    List<Job> findByStatus(JobStatus status);

    @Query("SELECT j FROM Job j WHERE " +
           "(:specialization IS NULL OR LOWER(j.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "j.status = 'ACTIVE' AND " +
           "(j.deadline IS NULL OR j.deadline > CURRENT_TIMESTAMP) " +
           "ORDER BY j.createdAt DESC")
    List<Job> searchJobs(@Param("specialization") String specialization,
                          @Param("location") String location,
                          @Param("jobType") JobType jobType);

    @Query("SELECT j FROM Job j WHERE j.status = 'ACTIVE' AND j.deadline IS NOT NULL AND j.deadline <= CURRENT_TIMESTAMP")
    List<Job> findExpiredActiveJobs();

    long countByStatus(JobStatus status);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.organization.user.id = :orgUserId")
    long countByOrgUserId(@Param("orgUserId") Long orgUserId);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.organization.user.id = :orgUserId AND j.status = :status")
    long countByOrgUserIdAndStatus(@Param("orgUserId") Long orgUserId, @Param("status") JobStatus status);
}
