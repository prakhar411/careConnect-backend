package com.careconnect.repository;

import com.careconnect.entity.ManagementTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ManagementTeamRepository extends JpaRepository<ManagementTeamMember, Long> {
    List<ManagementTeamMember> findByOrganizationId(Long organizationId);
}
