package com.careconnect.repository;

import com.careconnect.entity.PlatformPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PlatformPolicyRepository extends JpaRepository<PlatformPolicy, Long> {
    List<PlatformPolicy> findAllByOrderByCreatedAtDesc();
    List<PlatformPolicy> findByCategoryOrderByCreatedAtDesc(String category);
}
