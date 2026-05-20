package com.careconnect.config;

import com.careconnect.entity.PlatformPolicy;
import com.careconnect.entity.User;
import com.careconnect.enums.UserRole;
import com.careconnect.repository.PlatformPolicyRepository;
import com.careconnect.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository           userRepository;
    private final PasswordEncoder          passwordEncoder;
    private final PlatformPolicyRepository policyRepository;

    private static final String ADMIN_USERNAME = "CareConnectAdmin";
    private static final String ADMIN_PASSWORD = "careconnect@123";

    @Override
    public void run(String... args) {
        seedAdmin();
        seedPolicies();
    }

    private void seedAdmin() {
        if (userRepository.existsByEmail(ADMIN_USERNAME)) {
            log.info("Platform Admin already exists: {}", ADMIN_USERNAME);
            return;
        }
        User admin = userRepository.findAll().stream()
                .filter(u -> u.getRole() == UserRole.PLATFORM_ADMIN)
                .findFirst()
                .orElseGet(() -> User.builder().role(UserRole.PLATFORM_ADMIN).build());
        admin.setEmail(ADMIN_USERNAME);
        admin.setPassword(passwordEncoder.encode(ADMIN_PASSWORD));
        userRepository.save(admin);
        log.info("Platform Admin ready — username: {} | password: {}", ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    private void seedPolicies() {
        if (policyRepository.count() > 0) return;

        List<PlatformPolicy> defaults = List.of(
            PlatformPolicy.builder()
                .title("HIPAA Patient Data Privacy & Confidentiality Policy")
                .category("HIPAA")
                .effectiveDate(LocalDate.of(2026, 1, 1))
                .content("All patient health information (PHI) must be handled in strict compliance with HIPAA regulations. " +
                         "Access to PHI is granted only on a need-to-know basis. " +
                         "Unauthorized disclosure, modification, or destruction of patient records is strictly prohibited and will result in immediate suspension and legal action.")
                .build(),

            PlatformPolicy.builder()
                .title("HIPAA Security Rule & Technical Safeguards")
                .category("HIPAA")
                .effectiveDate(LocalDate.of(2026, 1, 1))
                .content("All electronic PHI (ePHI) must be encrypted at rest and in transit using industry-standard encryption. " +
                         "Passwords must be BCrypt-hashed with minimum strength 10. " +
                         "Access controls, audit logs, and two-factor authentication mechanisms must be maintained. " +
                         "System access must be reviewed quarterly.")
                .build(),

            PlatformPolicy.builder()
                .title("GDPR Compliance & Data Subject Rights Policy")
                .category("GDPR")
                .effectiveDate(LocalDate.of(2026, 1, 1))
                .content("All users have the right to access, rectify, restrict, and request erasure of their personal data. " +
                         "Explicit informed consent must be obtained before processing personal data. " +
                         "Data must not be retained beyond the period necessary for its stated purpose. " +
                         "A Data Protection Officer must be designated for GDPR oversight.")
                .build(),

            PlatformPolicy.builder()
                .title("Patient Safety & Incident Reporting Protocol")
                .category("SAFETY")
                .effectiveDate(LocalDate.of(2026, 1, 15))
                .content("All adverse events, near-misses, and patient safety incidents must be reported within 24 hours using the platform incident module. " +
                         "Nurses must follow the five rights of medication administration at all times. " +
                         "Fall prevention protocols must be implemented for all high-risk patients. " +
                         "Critical incidents must be escalated to the supervisor immediately.")
                .build(),

            PlatformPolicy.builder()
                .title("Infection Control & Clinical Safety Standards")
                .category("SAFETY")
                .effectiveDate(LocalDate.of(2026, 1, 15))
                .content("All nursing staff must adhere to standard precautions including hand hygiene, appropriate PPE usage, and sterile technique for all invasive procedures. " +
                         "Annual infection control training is mandatory for all clinical staff. " +
                         "Any breach of infection control protocols must be reported immediately and documented.")
                .build(),

            PlatformPolicy.builder()
                .title("Electronic Health Records Access Control Policy")
                .category("DATA")
                .effectiveDate(LocalDate.of(2026, 2, 1))
                .content("Access to electronic health records is strictly role-based and fully audited. " +
                         "Nurses may only access records for patients currently under their care. " +
                         "All EHR access events are logged with timestamp, user ID, and action type. " +
                         "Unauthorized or excessive access will result in disciplinary action and account suspension.")
                .build(),

            PlatformPolicy.builder()
                .title("Data Breach Detection & Notification Policy")
                .category("DATA")
                .effectiveDate(LocalDate.of(2026, 2, 1))
                .content("Any suspected or confirmed data breach must be reported to the Platform Administrator within 72 hours of discovery. " +
                         "Affected users and relevant authorities must be notified promptly as required by law. " +
                         "A formal breach investigation must be conducted, documented, and remediation steps implemented. " +
                         "Breach records must be retained for a minimum of 3 years.")
                .build(),

            PlatformPolicy.builder()
                .title("Code of Professional Conduct & Ethics")
                .category("GENERAL")
                .effectiveDate(LocalDate.of(2026, 1, 1))
                .content("All nursing staff and organizational members must maintain the highest standards of professional practice, integrity, and patient dignity. " +
                         "Discrimination, harassment, fraud, or patient negligence are grounds for immediate account suspension and legal referral. " +
                         "Staff must uphold the organization's values, maintain confidentiality, and act in the best interest of patients at all times.")
                .build(),

            PlatformPolicy.builder()
                .title("Password Security & Authentication Standards")
                .category("GENERAL")
                .effectiveDate(LocalDate.of(2026, 1, 1))
                .content("All user passwords are BCrypt-hashed with strength 10. " +
                         "Passwords must be at least 8 characters and include uppercase, lowercase, numbers, and special characters. " +
                         "Platform Administrator credentials must be rotated every 90 days. " +
                         "Shared or default credentials are strictly prohibited. " +
                         "Full JWT authentication with token expiry will be enforced from Phase 2 onwards.")
                .build()
        );

        policyRepository.saveAll(defaults);
        log.info("Seeded {} default platform policies.", defaults.size());
    }
}
