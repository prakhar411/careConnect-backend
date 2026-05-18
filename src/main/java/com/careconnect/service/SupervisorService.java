package com.careconnect.service;

import com.careconnect.dto.request.EscalationRequest;
import com.careconnect.dto.request.HandoffNoteRequest;
import com.careconnect.dto.response.*;
import com.careconnect.entity.*;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.enums.JobStatus;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class SupervisorService {

    private final NurseApplicationRepository nurseAppRepo;
    private final JobRepository              jobRepo;
    private final NurseProfileRepository     nurseProfileRepo;
    private final ShiftRepository            shiftRepo;
    private final HandoffNoteRepository      handoffRepo;
    private final EscalationRepository       escalationRepo;
    private final NotificationService        notificationSvc;

    // ── Workload: all hired nurses + their active/completed job counts ────────

    @Transactional(readOnly = true)
    public List<NurseWorkloadResponse> getWorkload(Long orgUserId) {
        List<NurseApplication> approved =
                nurseAppRepo.findByOrgUserIdAndStatus(orgUserId, ApplicationStatus.APPROVED);

        Map<Long, NurseProfile> nurseMap    = new LinkedHashMap<>();
        Map<Long, Long>         activeMap   = new HashMap<>();
        Map<Long, Long>         completedMap = new HashMap<>();

        for (NurseApplication app : approved) {
            NurseProfile nurse = app.getNurse();
            Job          job   = app.getJob();
            nurseMap.put(nurse.getId(), nurse);

            if (job.getStatus() == JobStatus.ACTIVE) {
                activeMap.merge(nurse.getId(), 1L, Long::sum);
            } else if (job.getStatus() == JobStatus.CLOSED) {
                completedMap.merge(nurse.getId(), 1L, Long::sum);
            }
        }

        return nurseMap.values().stream().map(nurse -> {
            long active    = activeMap.getOrDefault(nurse.getId(), 0L);
            long completed = completedMap.getOrDefault(nurse.getId(), 0L);
            String status  = active == 0 ? "AVAILABLE" : active <= 2 ? "ACTIVE" : "OVERLOADED";

            return NurseWorkloadResponse.builder()
                    .nurseId(nurse.getId())
                    .nurseUserId(nurse.getUser() != null ? nurse.getUser().getId() : null)
                    .nurseName(nurse.getFullName())
                    .specialization(nurse.getSpecialization())
                    .experienceYears(nurse.getExperienceYears())
                    .licenseNumber(nurse.getLicenseNumber())
                    .activeJobs(active)
                    .completedJobs(completed)
                    .totalJobs(active + completed)
                    .workloadStatus(status)
                    .build();
        }).collect(Collectors.toList());
    }

    // ── Job Assignments: all org jobs + their assigned nurse ─────────────────

    @Transactional(readOnly = true)
    public List<JobAssignmentResponse> getJobAssignments(Long orgUserId) {
        List<Job> jobs = jobRepo.findByOrgUserId(orgUserId);

        return jobs.stream().map(job -> {
            Optional<NurseApplication> approvedApp = nurseAppRepo.findByJobId(job.getId())
                    .stream()
                    .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                    .findFirst();

            NurseProfile nurse = approvedApp.map(NurseApplication::getNurse).orElse(null);
            Long appId         = approvedApp.map(NurseApplication::getId).orElse(null);

            return JobAssignmentResponse.builder()
                    .jobId(job.getId())
                    .jobTitle(job.getJobTitle())
                    .department(job.getDepartment())
                    .jobType(job.getJobType() != null ? job.getJobType().name() : null)
                    .jobStatus(job.getStatus() != null ? job.getStatus().name() : null)
                    .location(job.getLocation())
                    .specialization(job.getSpecialization())
                    .openings(job.getOpenings())
                    .deadline(job.getDeadline())
                    .salaryMin(job.getSalaryMin())
                    .salaryMax(job.getSalaryMax())
                    .assignedNurseId(nurse != null ? nurse.getId() : null)
                    .assignedNurseUserId(nurse != null && nurse.getUser() != null ? nurse.getUser().getId() : null)
                    .assignedNurseName(nurse != null ? nurse.getFullName() : null)
                    .assignedNurseSpecialization(nurse != null ? nurse.getSpecialization() : null)
                    .applicationId(appId)
                    .build();
        }).collect(Collectors.toList());
    }

    // ── Shift Coverage (AC 16.3) ──────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<ShiftCoverageResponse> getOrgShifts(Long orgUserId) {
        return shiftRepo.findByOrgUserId(orgUserId).stream().map(s -> {
            NurseProfile nurse = s.getAppointment().getNurse();
            String coveringName = null;
            if (s.getCoveringNurseUserId() != null) {
                coveringName = nurseProfileRepo.findByUserId(s.getCoveringNurseUserId())
                        .map(NurseProfile::getFullName).orElse("Unknown");
            }
            String patientName = s.getAppointment().getPatientFirstName() + " "
                    + (s.getAppointment().getPatientLastName() != null ? s.getAppointment().getPatientLastName() : "");
            return ShiftCoverageResponse.builder()
                    .shiftId(s.getId())
                    .appointmentId(s.getAppointment().getId())
                    .shiftDate(s.getShiftDate())
                    .status(s.getStatus())
                    .originalRate(s.getOriginalRate())
                    .nurseProfileId(nurse != null ? nurse.getId() : null)
                    .nurseUserId(nurse != null && nurse.getUser() != null ? nurse.getUser().getId() : null)
                    .nurseName(nurse != null ? nurse.getFullName() : "Unknown")
                    .nurseSpecialization(nurse != null ? nurse.getSpecialization() : null)
                    .patientName(patientName.trim())
                    .coveringNurseUserId(s.getCoveringNurseUserId())
                    .coveringNurseName(coveringName)
                    .markedAt(s.getPatientMarkedAt())
                    .build();
        }).collect(Collectors.toList());
    }

    @Transactional
    public ShiftCoverageResponse assignShiftCoverage(Long orgUserId, Long shiftId, Long coveringNurseUserId) {
        Shift shift = shiftRepo.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Shift", shiftId));

        NurseProfile coveringNurse = nurseProfileRepo.findByUserId(coveringNurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", coveringNurseUserId));

        shift.setCoveringNurseUserId(coveringNurseUserId);
        shiftRepo.save(shift);

        NurseProfile originalNurse = shift.getAppointment().getNurse();
        if (originalNurse != null && originalNurse.getUser() != null) {
            notificationSvc.pushToUser(
                    originalNurse.getUser().getId(), "SHIFT_COVERAGE",
                    "Shift Coverage Assigned",
                    "Supervisor has assigned " + coveringNurse.getFullName() + " to cover your shift on " + shift.getShiftDate(),
                    shift.getId(), "SHIFT");
        }
        notificationSvc.pushToUser(
                coveringNurseUserId, "SHIFT_COVERAGE",
                "You have been assigned shift coverage",
                "Supervisor has assigned you to cover a shift on " + shift.getShiftDate(),
                shift.getId(), "SHIFT");

        log.info("Supervisor (org={}) assigned coverage for shift={} to nurse={}", orgUserId, shiftId, coveringNurseUserId);
        return getOrgShifts(orgUserId).stream()
                .filter(r -> r.getShiftId().equals(shiftId)).findFirst().orElseThrow();
    }

    // ── Handoff Notes (AC 16.4) ───────────────────────────────────────────────

    @Transactional
    public HandoffNoteResponse sendHandoff(Long orgUserId, HandoffNoteRequest req) {
        NurseProfile nurse = nurseProfileRepo.findByUserId(req.getNurseUserId())
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", req.getNurseUserId()));

        HandoffNote note = HandoffNote.builder()
                .senderOrgUserId(orgUserId)
                .recipientNurseUserId(req.getNurseUserId())
                .jobId(req.getJobId())
                .note(req.getNote())
                .build();
        handoffRepo.save(note);

        String jobTitle = null;
        if (req.getJobId() != null) {
            jobTitle = jobRepo.findById(req.getJobId()).map(Job::getJobTitle).orElse(null);
        }
        notificationSvc.pushToUser(req.getNurseUserId(), "HANDOFF",
                "New Handoff Note from Supervisor",
                req.getNote().length() > 80 ? req.getNote().substring(0, 80) + "…" : req.getNote(),
                note.getId(), "HANDOFF");

        log.info("Supervisor (org={}) sent handoff note to nurse={}", orgUserId, req.getNurseUserId());
        return HandoffNoteResponse.builder()
                .id(note.getId())
                .recipientNurseUserId(req.getNurseUserId())
                .nurseName(nurse.getFullName())
                .jobId(req.getJobId())
                .jobTitle(jobTitle)
                .note(req.getNote())
                .sentAt(note.getSentAt())
                .build();
    }

    @Transactional(readOnly = true)
    public List<HandoffNoteResponse> getHandoffs(Long orgUserId) {
        return handoffRepo.findBySenderOrgUserIdOrderBySentAtDesc(orgUserId).stream().map(n -> {
            String nurseName = nurseProfileRepo.findByUserId(n.getRecipientNurseUserId())
                    .map(NurseProfile::getFullName).orElse("Unknown");
            String jobTitle = null;
            if (n.getJobId() != null) {
                jobTitle = jobRepo.findById(n.getJobId()).map(Job::getJobTitle).orElse(null);
            }
            return HandoffNoteResponse.builder()
                    .id(n.getId())
                    .recipientNurseUserId(n.getRecipientNurseUserId())
                    .nurseName(nurseName)
                    .jobId(n.getJobId())
                    .jobTitle(jobTitle)
                    .note(n.getNote())
                    .sentAt(n.getSentAt())
                    .build();
        }).collect(Collectors.toList());
    }

    // ── Escalations (AC 16.7) ─────────────────────────────────────────────────

    @Transactional
    public EscalationResponse createEscalation(Long orgUserId, EscalationRequest req) {
        Escalation e = Escalation.builder()
                .orgUserId(orgUserId)
                .issueType(req.getIssueType())
                .entityType(req.getEntityType())
                .entityId(req.getEntityId())
                .description(req.getDescription())
                .build();
        escalationRepo.save(e);
        log.info("Supervisor (org={}) raised escalation type={}", orgUserId, req.getIssueType());
        return toEscalationResponse(e);
    }

    @Transactional(readOnly = true)
    public List<EscalationResponse> getEscalations(Long orgUserId) {
        return escalationRepo.findByOrgUserIdOrderByCreatedAtDesc(orgUserId)
                .stream().map(this::toEscalationResponse).collect(Collectors.toList());
    }

    @Transactional
    public EscalationResponse resolveEscalation(Long orgUserId, Long escalationId) {
        Escalation e = escalationRepo.findById(escalationId)
                .orElseThrow(() -> new ResourceNotFoundException("Escalation", escalationId));
        if (!e.getOrgUserId().equals(orgUserId)) throw new BadRequestException("Not your escalation.");
        e.setStatus("RESOLVED");
        e.setResolvedAt(LocalDateTime.now());
        escalationRepo.save(e);
        return toEscalationResponse(e);
    }

    private EscalationResponse toEscalationResponse(Escalation e) {
        String entityName = null;
        if ("NURSE".equals(e.getEntityType()) && e.getEntityId() != null) {
            entityName = nurseProfileRepo.findByUserId(e.getEntityId())
                    .map(NurseProfile::getFullName).orElse(null);
        } else if ("JOB".equals(e.getEntityType()) && e.getEntityId() != null) {
            entityName = jobRepo.findById(e.getEntityId()).map(Job::getJobTitle).orElse(null);
        }
        return EscalationResponse.builder()
                .id(e.getId()).issueType(e.getIssueType())
                .entityType(e.getEntityType()).entityId(e.getEntityId())
                .entityName(entityName).description(e.getDescription())
                .status(e.getStatus()).createdAt(e.getCreatedAt()).resolvedAt(e.getResolvedAt())
                .build();
    }

    // ── Assign nurse directly to a job (supervisor action) ───────────────────

    @Transactional
    public JobAssignmentResponse assignNurse(Long orgUserId, Long jobId, Long nurseUserId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (!job.getOrganization().getUser().getId().equals(orgUserId)) {
            throw new BadRequestException("This job does not belong to your organization.");
        }

        NurseProfile nurse = nurseProfileRepo.findByUserId(nurseUserId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseUserId));

        // Check if already assigned to this job
        if (nurseAppRepo.existsByNurseIdAndJobId(nurse.getId(), jobId)) {
            // Find and approve existing application
            nurseAppRepo.findByJobId(jobId).stream()
                    .filter(a -> a.getNurse().getId().equals(nurse.getId()))
                    .forEach(a -> { a.setStatus(ApplicationStatus.APPROVED); nurseAppRepo.save(a); });
        } else {
            // Create new approved application
            NurseApplication newApp = NurseApplication.builder()
                    .nurse(nurse)
                    .job(job)
                    .status(ApplicationStatus.APPROVED)
                    .coverNote("Directly assigned by supervisor")
                    .build();
            nurseAppRepo.save(newApp);
        }

        log.info("Supervisor (org={}) assigned nurse={} to job={}", orgUserId, nurseUserId, jobId);

        // Return updated job assignment
        return getJobAssignments(orgUserId).stream()
                .filter(ja -> ja.getJobId().equals(jobId))
                .findFirst()
                .orElseThrow();
    }

    // ── Reassign: reject old nurse, approve new nurse ─────────────────────────

    @Transactional
    public JobAssignmentResponse reassignNurse(Long orgUserId, Long jobId, Long newNurseUserId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new ResourceNotFoundException("Job", jobId));

        if (!job.getOrganization().getUser().getId().equals(orgUserId)) {
            throw new BadRequestException("This job does not belong to your organization.");
        }

        // Reject all currently approved applications for this job
        nurseAppRepo.findByJobId(jobId).stream()
                .filter(a -> a.getStatus() == ApplicationStatus.APPROVED)
                .forEach(a -> { a.setStatus(ApplicationStatus.REJECTED); nurseAppRepo.save(a); });

        // Assign new nurse
        return assignNurse(orgUserId, jobId, newNurseUserId);
    }
}
