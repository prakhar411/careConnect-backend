# CareConnect — Entity Relationship Diagram

> Render this file in VS Code with the **Markdown Preview Mermaid Support** extension,
> or paste the diagram block into [mermaid.live](https://mermaid.live).

```mermaid
erDiagram

    %% ── USERS (base auth table) ──────────────────────────────────
    users {
        BIGINT      id              PK
        VARCHAR     email           UK
        VARCHAR     password
        ENUM        role            "PATIENT | NURSE | ORGANIZATION"
        BOOLEAN     is_active
        DATETIME    created_at
        DATETIME    updated_at
    }

    %% ── PATIENT PROFILES ─────────────────────────────────────────
    patient_profiles {
        BIGINT      id              PK
        BIGINT      user_id         FK
        VARCHAR     full_name
        DATE        date_of_birth
        VARCHAR     blood_group
        VARCHAR     phone
        TEXT        address
        VARCHAR     city
        VARCHAR     state
        VARCHAR     emergency_contact
        VARCHAR     emergency_contact_phone
        TEXT        medical_history
        TEXT        allergies
        TEXT        current_medications
    }

    %% ── NURSE PROFILES ───────────────────────────────────────────
    nurse_profiles {
        BIGINT      id              PK
        BIGINT      user_id         FK
        VARCHAR     full_name
        VARCHAR     license_number  UK
        VARCHAR     phone
        VARCHAR     specialization
        VARCHAR     education
        INT         experience_years
        VARCHAR     expertise
        TEXT        previous_employment
        TEXT        professional_references
        VARCHAR     availability
        DOUBLE      rating
        VARCHAR     profile_status
    }

    %% ── ORGANIZATIONS ────────────────────────────────────────────
    organizations {
        BIGINT      id              PK
        BIGINT      user_id         FK
        VARCHAR     org_name
        VARCHAR     org_type
        VARCHAR     reg_number      UK
        VARCHAR     contact_person
        VARCHAR     designation
        VARCHAR     phone
        TEXT        address
        VARCHAR     city
        VARCHAR     state
        VARCHAR     pincode
        VARCHAR     website
        VARCHAR     status
        DATETIME    created_at
    }

    %% ── JOBS ─────────────────────────────────────────────────────
    jobs {
        BIGINT      id              PK
        BIGINT      organization_id FK
        VARCHAR     job_title
        VARCHAR     department
        VARCHAR     location
        ENUM        job_type        "TEMPORARY | PERMANENT | EMERGENCY | CONTRACT"
        VARCHAR     specialization
        DECIMAL     salary_min
        DECIMAL     salary_max
        VARCHAR     shift_details
        VARCHAR     patient_acuity
        TEXT        description
        VARCHAR     priority
        DATE        deadline
        ENUM        status          "ACTIVE | CLOSED | DRAFT"
        DATETIME    created_at
    }

    %% ── APPOINTMENTS ─────────────────────────────────────────────
    appointments {
        BIGINT      id              PK
        BIGINT      patient_id      FK
        BIGINT      nurse_id        FK "nullable"
        DATETIME    appointment_date
        DATETIME    end_date
        VARCHAR     care_needs
        VARCHAR     required_skills
        VARCHAR     duration
        TEXT        notes
        ENUM        status          "PENDING | CONFIRMED | IN_PROGRESS | COMPLETED | CANCELLED"
        DATETIME    created_at
    }

    %% ── MEDICAL RECORDS ──────────────────────────────────────────
    medical_records {
        BIGINT      id              PK
        BIGINT      patient_id      FK
        BIGINT      uploaded_by     FK "nullable"
        VARCHAR     record_type
        VARCHAR     title
        TEXT        description
        VARCHAR     file_url
        VARCHAR     file_name
        DATETIME    created_at
    }

    %% ── NURSE APPLICATIONS ───────────────────────────────────────
    nurse_applications {
        BIGINT      id              PK
        BIGINT      nurse_id        FK
        BIGINT      job_id          FK
        TEXT        cover_note
        ENUM        status          "PENDING | APPROVED | REJECTED"
        DATETIME    applied_at
    }

    %% ── COMPLIANCE RECORDS ───────────────────────────────────────
    compliance_records {
        BIGINT      id              PK
        BIGINT      organization_id FK
        VARCHAR     nurse_name
        VARCHAR     requirement
        DATE        due_date
        TEXT        notes
        ENUM        status          "COMPLIANT | PENDING | NON_COMPLIANT"
        DATETIME    created_at
    }

    %% ── CREDENTIALS ──────────────────────────────────────────────
    credentials {
        BIGINT      id              PK
        BIGINT      nurse_id        FK
        VARCHAR     credential_type
        VARCHAR     issued_by
        DATE        issued_date
        DATE        expiry_date
        VARCHAR     document_url
        ENUM        status          "VERIFIED | PENDING | EXPIRED"
        DATETIME    created_at
    }

    %% ── PAYMENTS ─────────────────────────────────────────────────
    payments {
        BIGINT      id              PK
        BIGINT      nurse_id        FK
        BIGINT      appointment_id  FK "nullable"
        DECIMAL     amount
        VARCHAR     payment_method
        VARCHAR     payment_structure
        DECIMAL     hours_worked
        DECIMAL     tax_deduction
        TEXT        description
        ENUM        status          "PENDING | PROCESSED | FAILED | REFUNDED"
        DATETIME    payment_date
    }

    %% ── RELATIONSHIPS ────────────────────────────────────────────

    users             ||--o| patient_profiles   : "1:1  has profile"
    users             ||--o| nurse_profiles     : "1:1  has profile"
    users             ||--o| organizations      : "1:1  has profile"

    patient_profiles  ||--o{ appointments       : "1:N  books"
    nurse_profiles    ||--o{ appointments       : "1:N  assigned to"

    patient_profiles  ||--o{ medical_records    : "1:N  owns"
    users             ||--o{ medical_records    : "1:N  uploads"

    organizations     ||--o{ jobs               : "1:N  posts"
    organizations     ||--o{ compliance_records : "1:N  tracks"

    nurse_profiles    ||--o{ nurse_applications : "1:N  applies"
    jobs              ||--o{ nurse_applications : "1:N  receives"

    nurse_profiles    ||--o{ credentials        : "1:N  holds"
    nurse_profiles    ||--o{ payments           : "1:N  receives"

    appointments      ||--o{ payments           : "1:N  generates"
```

---

## Relationship Reference

| Table | Related To | Type | Via Column |
|---|---|---|---|
| `patient_profiles` | `users` | 1:1 | `user_id` |
| `nurse_profiles` | `users` | 1:1 | `user_id` |
| `organizations` | `users` | 1:1 | `user_id` |
| `jobs` | `organizations` | N:1 | `organization_id` |
| `appointments` | `patient_profiles` | N:1 | `patient_id` |
| `appointments` | `nurse_profiles` | N:1 (nullable) | `nurse_id` |
| `medical_records` | `patient_profiles` | N:1 | `patient_id` |
| `medical_records` | `users` | N:1 (nullable) | `uploaded_by` |
| `nurse_applications` | `nurse_profiles` | N:1 | `nurse_id` |
| `nurse_applications` | `jobs` | N:1 | `job_id` |
| `compliance_records` | `organizations` | N:1 | `organization_id` |
| `credentials` | `nurse_profiles` | N:1 | `nurse_id` |
| `payments` | `nurse_profiles` | N:1 | `nurse_id` |
| `payments` | `appointments` | N:1 (nullable) | `appointment_id` |

## Indexes Summary

| Table | Index | Purpose |
|---|---|---|
| `users` | `uq_users_email` | Fast login lookup |
| `nurse_profiles` | `uq_nurse_license` | License number uniqueness |
| `organizations` | `uq_org_reg_number` | Registration number uniqueness |
| `nurse_applications` | `uq_nurse_job_application` | Prevent duplicate applications |
| `jobs` | `idx_jobs_status` | Filter active jobs quickly |
| `appointments` | `idx_appt_status` | Filter by appointment status |
| `credentials` | `idx_cred_expiry` | Query expiring credentials |
| `payments` | `idx_pay_status` | Filter pending/processed payments |
