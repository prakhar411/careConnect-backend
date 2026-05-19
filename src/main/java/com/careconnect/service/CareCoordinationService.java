package com.careconnect.service;

import com.careconnect.dto.request.CareTeamRequest;
import com.careconnect.dto.request.ProviderNoteRequest;
import com.careconnect.entity.CareGoal;
import com.careconnect.entity.CareTeamMember;
import com.careconnect.entity.ManagementTeamMember;
import com.careconnect.entity.ProviderNote;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.CareGoalRepository;
import com.careconnect.repository.CareTeamMemberRepository;
import com.careconnect.repository.ManagementTeamRepository;
import com.careconnect.repository.ProviderNoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CareCoordinationService {

    private final CareTeamMemberRepository careTeamRepo;
    private final ProviderNoteRepository   noteRepo;
    private final ManagementTeamRepository teamRepo;
    private final CareGoalRepository       goalRepo;

    // ── Care Team ─────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> addToTeam(Long patientUserId, CareTeamRequest req) {
        ManagementTeamMember member = teamRepo.findById(req.getTeamMemberId())
                .orElseThrow(() -> new ResourceNotFoundException("TeamMember", req.getTeamMemberId()));

        if (careTeamRepo.existsByPatientUserIdAndTeamMemberId(patientUserId, req.getTeamMemberId())) {
            throw new BadRequestException(member.getName() + " is already in this patient's care team.");
        }

        CareTeamMember entry = CareTeamMember.builder()
                .patientUserId(patientUserId)
                .appointmentId(req.getAppointmentId())
                .teamMemberId(member.getId())
                .memberName(member.getName())
                .memberRole(member.getRole())
                .addedByNurseUserId(req.getNurseUserId())
                .addedByNurseName(req.getNurseName())
                .build();

        CareTeamMember saved = careTeamRepo.save(entry);
        return toTeamMap(saved);
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getTeam(Long patientUserId) {
        return careTeamRepo.findByPatientUserId(patientUserId)
                .stream().map(this::toTeamMap).collect(Collectors.toList());
    }

    @Transactional
    public void removeFromTeam(Long entryId) {
        if (!careTeamRepo.existsById(entryId))
            throw new ResourceNotFoundException("CareTeamMember", entryId);
        careTeamRepo.deleteById(entryId);
    }

    // ── Provider Notes ────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> addNote(Long patientUserId, ProviderNoteRequest req) {
        ProviderNote note = ProviderNote.builder()
                .patientUserId(patientUserId)
                .authorNurseUserId(req.getAuthorNurseUserId())
                .authorName(req.getAuthorName() != null ? req.getAuthorName() : "Nurse")
                .authorRole(req.getAuthorRole() != null ? req.getAuthorRole() : "Nurse")
                .content(req.getContent())
                .noteType(req.getNoteType() != null ? req.getNoteType() : "CLINICAL_UPDATE")
                .build();
        return toNoteMap(noteRepo.save(note));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNotes(Long patientUserId) {
        return noteRepo.findByPatientUserIdOrderByCreatedAtDesc(patientUserId)
                .stream().map(this::toNoteMap).collect(Collectors.toList());
    }

    // ── All org team members (for nurse dropdown) ─────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllTeamMembers() {
        return teamRepo.findAll().stream().map(m -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id",   m.getId());
            map.put("name", m.getName());
            map.put("role", m.getRole());
            map.put("status", m.getStatus());
            return map;
        }).collect(Collectors.toList());
    }

    // ── Care Goals ────────────────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> addGoal(Long patientUserId, Map<String, Object> req) {
        CareGoal goal = CareGoal.builder()
                .patientUserId(patientUserId)
                .addedByNurseUserId(req.get("nurseUserId") != null
                        ? Long.valueOf(req.get("nurseUserId").toString()) : null)
                .addedByNurseName(req.getOrDefault("nurseName", "Nurse").toString())
                .goalText(req.get("goalText").toString())
                .targetDate(req.get("targetDate") != null
                        ? java.time.LocalDate.parse(req.get("targetDate").toString()) : null)
                .build();
        return toGoalMap(goalRepo.save(goal));
    }

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getGoals(Long patientUserId) {
        return goalRepo.findByPatientUserIdOrderByCreatedAtDesc(patientUserId)
                .stream().map(this::toGoalMap).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> updateGoalStatus(Long goalId, String status) {
        CareGoal goal = goalRepo.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("CareGoal", goalId));
        goal.setStatus(status);
        return toGoalMap(goalRepo.save(goal));
    }

    @Transactional
    public void deleteGoal(Long goalId) {
        if (!goalRepo.existsById(goalId)) throw new ResourceNotFoundException("CareGoal", goalId);
        goalRepo.deleteById(goalId);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Map<String, Object> toTeamMap(CareTeamMember m) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",               m.getId());
        map.put("patientUserId",    m.getPatientUserId());
        map.put("teamMemberId",     m.getTeamMemberId());
        map.put("memberName",       m.getMemberName());
        map.put("memberRole",       m.getMemberRole());
        map.put("addedByNurseName", m.getAddedByNurseName());
        map.put("createdAt",        m.getCreatedAt());
        return map;
    }

    private Map<String, Object> toGoalMap(CareGoal g) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",               g.getId());
        map.put("patientUserId",    g.getPatientUserId());
        map.put("goalText",         g.getGoalText());
        map.put("targetDate",       g.getTargetDate());
        map.put("status",           g.getStatus());
        map.put("addedByNurseName", g.getAddedByNurseName());
        map.put("createdAt",        g.getCreatedAt());
        return map;
    }

    private Map<String, Object> toNoteMap(ProviderNote n) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id",               n.getId());
        map.put("patientUserId",    n.getPatientUserId());
        map.put("authorName",       n.getAuthorName());
        map.put("authorRole",       n.getAuthorRole());
        map.put("content",          n.getContent());
        map.put("noteType",         n.getNoteType());
        map.put("createdAt",        n.getCreatedAt());
        return map;
    }
}
