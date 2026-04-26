package com.careconnect.repository;

import com.careconnect.entity.NurseProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NurseProfileRepository extends JpaRepository<NurseProfile, Long> {
    Optional<NurseProfile> findByUserId(Long userId);
    Optional<NurseProfile> findByLicenseNumber(String licenseNumber);
    boolean existsByLicenseNumber(String licenseNumber);

    @Query("SELECT n FROM NurseProfile n WHERE " +
           "(:specialization IS NULL OR LOWER(n.specialization) LIKE LOWER(CONCAT('%', :specialization, '%'))) AND " +
           "(:availability IS NULL OR n.availability = :availability)")
    List<NurseProfile> searchNurses(@Param("specialization") String specialization,
                                    @Param("availability") String availability);
}
