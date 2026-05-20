package com.careconnect.service;

import com.careconnect.entity.NurseProfile;
import com.careconnect.entity.PlatformPolicy;
import com.careconnect.entity.TrainingRecord;
import com.careconnect.entity.User;
import com.careconnect.enums.ApplicationStatus;
import com.careconnect.enums.ComplianceStatus;
import com.careconnect.enums.CredentialStatus;
import com.careconnect.enums.UserRole;
import com.careconnect.exception.BadRequestException;
import com.careconnect.exception.ResourceNotFoundException;
import com.careconnect.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlatformAdminService {

    private final UserRepository             userRepo;
    private final NurseProfileRepository     nurseRepo;
    private final PatientProfileRepository   patientRepo;
    private final OrganizationRepository     orgRepo;
    private final AppointmentRepository      apptRepo;
    private final ComplianceRecordRepository complianceRepo;
    private final CredentialRepository       credentialRepo;
    private final EscalationRepository       escalationRepo;
    private final NurseApplicationRepository applicationRepo;
    private final PlatformPolicyRepository   policyRepo;
    private final MedicalRecordRepository    medicalRecordRepo;
    private final TrainingRecordRepository   trainingRecordRepo;

    // ── Platform-wide stats ───────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public Map<String, Object> getStats() {
        long totalNurses       = userRepo.countByRole(UserRole.NURSE);
        long totalPatients     = userRepo.countByRole(UserRole.PATIENT);
        long totalOrgs         = userRepo.countByRole(UserRole.ORGANIZATION);
        long totalAppointments = apptRepo.count();
        long compliantCount    = complianceRepo.countByStatus(ComplianceStatus.COMPLIANT);
        long nonCompliantCount = complianceRepo.countByStatus(ComplianceStatus.NON_COMPLIANT);
        long totalCompliance   = complianceRepo.count();
        long expiredCreds      = credentialRepo.countByStatus(CredentialStatus.EXPIRED);
        long openEscalations   = escalationRepo.countByStatus("OPEN");
        long totalApplications = applicationRepo.count();
        long approvedApps      = applicationRepo.countByStatus(ApplicationStatus.APPROVED);

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("totalNurses",       totalNurses);
        stats.put("totalPatients",     totalPatients);
        stats.put("totalOrgs",         totalOrgs);
        stats.put("totalAppointments", totalAppointments);
        stats.put("compliantCount",    compliantCount);
        stats.put("nonCompliantCount", nonCompliantCount);
        stats.put("totalCompliance",   totalCompliance);
        stats.put("complianceRate",    totalCompliance > 0
                ? Math.round((compliantCount * 100.0) / totalCompliance) : 0);
        stats.put("expiredCredentials", expiredCreds);
        stats.put("openEscalations",    openEscalations);
        stats.put("totalApplications",  totalApplications);
        stats.put("approvedApplications", approvedApps);
        stats.put("hireRate",           totalApplications > 0
                ? Math.round((approvedApps * 100.0) / totalApplications) : 0);

        // Avg nurse rating (only nurses who have been rated)
        double avgRating = nurseRepo.findAll().stream()
                .filter(n -> n.getRating() != null && n.getRating() > 0)
                .mapToDouble(n -> n.getRating())
                .average().orElse(0.0);
        stats.put("avgNurseRating", Math.round(avgRating * 10.0) / 10.0);

        return stats;
    }

    // ── All compliance records (cross-org) ────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllCompliance() {
        return complianceRepo.findAll().stream().map(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",              c.getId());
            m.put("nurseName",       c.getNurseName());
            m.put("requirement",     c.getRequirement());
            m.put("dueDate",         c.getDueDate());
            m.put("status",          c.getStatus());
            m.put("notes",           c.getNotes());
            m.put("createdAt",       c.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    // ── All escalations (cross-org) ───────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllEscalations() {
        return escalationRepo.findAll().stream().map(e -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",          e.getId());
            m.put("issueType",   e.getIssueType());
            m.put("description", e.getDescription());
            m.put("status",      e.getStatus());
            m.put("createdAt",   e.getCreatedAt());
            m.put("resolvedAt",  e.getResolvedAt());
            return m;
        }).collect(Collectors.toList());
    }

    // ── All users (cross-platform) ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllUsers() {
        return userRepo.findAll().stream()
                .filter(u -> u.getRole() != UserRole.PLATFORM_ADMIN)
                .map(u -> {
                    Map<String, Object> m = new LinkedHashMap<>();
                    m.put("id",        u.getId());
                    m.put("email",     u.getEmail());
                    m.put("role",      u.getRole().name());
                    m.put("isActive",  u.getIsActive());
                    m.put("createdAt", u.getCreatedAt());
                    return m;
                }).collect(Collectors.toList());
    }

    // ── Toggle User Status (Suspend / Activate) ───────────────────────────────

    @Transactional
    public Map<String, Object> toggleUserStatus(Long userId) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", userId));
        if (user.getRole() == UserRole.PLATFORM_ADMIN) {
            throw new BadRequestException("Cannot suspend the platform administrator.");
        }
        user.setIsActive(!user.getIsActive());
        userRepo.save(user);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",       user.getId());
        m.put("email",    user.getEmail());
        m.put("role",     user.getRole().name());
        m.put("isActive", user.getIsActive());
        return m;
    }

    // ── Audit Trail (Medical Records + Compliance + Credentials) ─────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAuditTrail() {
        List<Map<String, Object>> events = new ArrayList<>();

        // Medical records
        medicalRecordRepo.findAll().forEach(r -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("eventType",   "MEDICAL_RECORD");
            m.put("icon",        "bi-journal-medical");
            String patientName = "—";
            try { patientName = r.getPatient().getFullName(); } catch (Exception ignored) {}
            String uploaderEmail = "—";
            try { uploaderEmail = r.getUploadedBy() != null ? r.getUploadedBy().getEmail() : "Patient"; } catch (Exception ignored) {}
            m.put("description", r.getRecordType() + ": " + (r.getTitle() != null ? r.getTitle() : "—"));
            m.put("entity",      "Patient: " + patientName);
            m.put("actor",       "By: " + uploaderEmail);
            m.put("date",        r.getCreatedAt());
            events.add(m);
        });

        // Compliance records
        complianceRepo.findAll().forEach(c -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("eventType",   "COMPLIANCE");
            m.put("icon",        "bi-clipboard2-check");
            m.put("description", c.getRequirement() + " — " + c.getStatus());
            m.put("entity",      "Nurse: " + (c.getNurseName() != null ? c.getNurseName() : "—"));
            m.put("actor",       "Status: " + c.getStatus());
            m.put("date",        c.getCreatedAt());
            events.add(m);
        });

        // Credentials
        credentialRepo.findAll().forEach(cr -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("eventType",   "CREDENTIAL");
            m.put("icon",        "bi-award");
            String nurseName = "—";
            try { nurseName = cr.getNurse().getFullName(); } catch (Exception ignored) {}
            m.put("description", cr.getCredentialType() + " — " + cr.getStatus());
            m.put("entity",      "Nurse: " + nurseName);
            m.put("actor",       "Issuer: " + (cr.getIssuedBy() != null ? cr.getIssuedBy() : "—"));
            m.put("date",        cr.getCreatedAt());
            events.add(m);
        });

        // Sort by date DESC, return latest 80
        return events.stream()
                .filter(e -> e.get("date") != null)
                .sorted(Comparator.comparing(e -> ((java.time.LocalDateTime) e.get("date"))))
                .collect(Collectors.collectingAndThen(Collectors.toList(), list -> {
                    Collections.reverse(list);
                    return list.stream().limit(80).collect(Collectors.toList());
                }));
    }

    // ── Verify Nurse License ──────────────────────────────────────────────────

    @Transactional
    public Map<String, Object> verifyNurseLicense(Long nurseProfileId) {
        NurseProfile nurse = nurseRepo.findById(nurseProfileId)
                .orElseThrow(() -> new ResourceNotFoundException("NurseProfile", nurseProfileId));
        nurse.setVerifiedByAdmin(!Boolean.TRUE.equals(nurse.getVerifiedByAdmin()));
        nurseRepo.save(nurse);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",              nurse.getId());
        m.put("fullName",        nurse.getFullName());
        m.put("verifiedByAdmin", nurse.getVerifiedByAdmin());
        return m;
    }

    // ── All Organizations + verify ────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getAllOrganizations() {
        return orgRepo.findAll().stream().map(o -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",              o.getId());
            m.put("orgName",         o.getOrgName());
            m.put("orgType",         o.getOrgType());
            m.put("regNumber",       o.getRegNumber());
            m.put("licenseNumber",   o.getLicenseNumber());
            m.put("phone",           o.getPhone());
            m.put("accreditation",   o.getAccreditation());
            m.put("status",          o.getStatus());
            m.put("verifiedByAdmin", o.getVerifiedByAdmin());
            m.put("createdAt",       o.getCreatedAt());
            return m;
        }).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> verifyOrganization(Long orgId) {
        var org = orgRepo.findById(orgId)
                .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));
        org.setVerifiedByAdmin(!Boolean.TRUE.equals(org.getVerifiedByAdmin()));
        orgRepo.save(org);
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",              org.getId());
        m.put("orgName",         org.getOrgName());
        m.put("verifiedByAdmin", org.getVerifiedByAdmin());
        return m;
    }

    // ── Nurse Profiles for Staff Verification ─────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getNurseProfiles() {
        // Pre-compute credential counts by nurseProfile.id
        Map<Long, Long> credCounts = credentialRepo.findAll().stream()
                .collect(Collectors.groupingBy(c -> c.getNurse().getId(), Collectors.counting()));

        // Pre-compute training counts by nurseUserId
        Map<Long, Long> trainCounts = trainingRecordRepo.findAll().stream()
                .collect(Collectors.groupingBy(TrainingRecord::getNurseUserId, Collectors.counting()));

        return nurseRepo.findAll().stream().map(n -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id",            n.getId());
            m.put("userId",        n.getUser().getId());
            m.put("fullName",      n.getFullName());
            m.put("licenseNumber", n.getLicenseNumber());
            m.put("specialization",n.getSpecialization());
            m.put("rating",        n.getRating());
            long creds    = credCounts.getOrDefault(n.getId(), 0L);
            long training = trainCounts.getOrDefault(n.getUser().getId(), 0L);
            m.put("credentialCount", creds);
            m.put("trainingCount",   training);
            // Onboarding: Complete = has creds AND training, Partial = one, None = neither
            String onboarding = (creds > 0 && training > 0) ? "COMPLETE"
                               : (creds > 0 || training > 0) ? "PARTIAL" : "NONE";
            m.put("onboardingStatus", onboarding);
            m.put("verifiedByAdmin",  Boolean.TRUE.equals(n.getVerifiedByAdmin()));
            return m;
        }).collect(Collectors.toList());
    }

    // ── Platform Policies ─────────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<Map<String, Object>> getPolicies() {
        return policyRepo.findAllByOrderByCreatedAtDesc().stream()
                .map(this::toPolicyMap).collect(Collectors.toList());
    }

    @Transactional
    public Map<String, Object> addPolicy(Map<String, Object> req) {
        PlatformPolicy policy = PlatformPolicy.builder()
                .title(req.get("title").toString())
                .content(req.getOrDefault("content", "").toString())
                .category(req.getOrDefault("category", "GENERAL").toString())
                .effectiveDate(req.get("effectiveDate") != null
                        ? LocalDate.parse(req.get("effectiveDate").toString()) : null)
                .addedByAdminId(req.get("adminId") != null
                        ? Long.valueOf(req.get("adminId").toString()) : null)
                .build();
        return toPolicyMap(policyRepo.save(policy));
    }

    @Transactional
    public void deletePolicy(Long id) {
        if (!policyRepo.existsById(id)) throw new ResourceNotFoundException("Policy", id);
        policyRepo.deleteById(id);
    }

    private Map<String, Object> toPolicyMap(PlatformPolicy p) {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id",            p.getId());
        m.put("title",         p.getTitle());
        m.put("content",       p.getContent());
        m.put("category",      p.getCategory());
        m.put("effectiveDate", p.getEffectiveDate());
        m.put("createdAt",     p.getCreatedAt());
        return m;
    }
}
