package com.careconnect.repository;

import com.careconnect.entity.Escalation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EscalationRepository extends JpaRepository<Escalation, Long> {
    List<Escalation> findByOrgUserIdOrderByCreatedAtDesc(Long orgUserId);
    long countByStatus(String status);
}
