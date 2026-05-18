package com.careconnect.repository;

import com.careconnect.entity.HandoffNote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HandoffNoteRepository extends JpaRepository<HandoffNote, Long> {
    List<HandoffNote> findBySenderOrgUserIdOrderBySentAtDesc(Long senderOrgUserId);
}
