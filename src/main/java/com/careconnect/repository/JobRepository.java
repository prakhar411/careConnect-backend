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
    List<Job> findByOrganizationId(Long organizationId);
    List<Job> findByStatus(JobStatus status);

    @Query("SELECT j FROM Job j WHERE " +
           "(:specialization IS NULL OR LOWER(j.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:location IS NULL OR LOWER(j.location) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "j.status = 'ACTIVE'")
    List<Job> searchJobs(@Param("specialization") String specialization,
                          @Param("location") String location,
                          @Param("jobType") JobType jobType);

    long countByStatus(JobStatus status);
}
