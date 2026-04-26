package com.careconnect.repository;

import com.careconnect.entity.ComplianceRecord;
import com.careconnect.enums.ComplianceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRecordRepository extends JpaRepository<ComplianceRecord, Long> {
    List<ComplianceRecord> findByOrganizationId(Long organizationId);
    List<ComplianceRecord> findByStatus(ComplianceStatus status);
    long countByStatus(ComplianceStatus status);
}
