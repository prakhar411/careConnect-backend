package com.careconnect.repository;

import com.careconnect.entity.Organization;
import com.careconnect.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, Long> {
    Optional<Organization> findByUserId(Long userId);
    Optional<Organization> findByRegNumber(String regNumber);
    boolean existsByRegNumber(String regNumber);

    @Query("SELECT o.user FROM Organization o WHERE o.regNumber = :regNumber")
    Optional<User> findUserByRegNumber(@Param("regNumber") String regNumber);
}
