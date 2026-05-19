package com.careconnect.repository;

import com.careconnect.entity.ProviderNote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProviderNoteRepository extends JpaRepository<ProviderNote, Long> {
    List<ProviderNote> findByPatientUserIdOrderByCreatedAtDesc(Long patientUserId);
}
