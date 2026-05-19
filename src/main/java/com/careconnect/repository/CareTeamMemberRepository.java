package com.careconnect.repository;

import com.careconnect.entity.CareTeamMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareTeamMemberRepository extends JpaRepository<CareTeamMember, Long> {
    List<CareTeamMember> findByPatientUserId(Long patientUserId);
    boolean existsByPatientUserIdAndTeamMemberId(Long patientUserId, Long teamMemberId);
}
