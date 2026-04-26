package com.careconnect.repository;

import com.careconnect.entity.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUserId(Long userId);
    Optional<Organization> findByRegNumber(String regNumber);
    boolean existsByRegNumber(String regNumber);
}
