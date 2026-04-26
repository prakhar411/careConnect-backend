package com.careconnect.repository;

import com.careconnect.entity.Credential;
import com.careconnect.enums.CredentialStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface CredentialRepository extends JpaRepository<Credential, Long> {
    List<Credential> findByNurseId(Long nurseId);
    List<Credential> findByStatus(CredentialStatus status);

    @Query("SELECT c FROM Credential c WHERE c.expiryDate <= :thresholdDate AND c.status = 'VERIFIED'")
    List<Credential> findExpiringSoon(LocalDate thresholdDate);

    long countByStatus(CredentialStatus status);
}
