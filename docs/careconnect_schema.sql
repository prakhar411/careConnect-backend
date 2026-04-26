-- ============================================================
--  CareConnect Database DDL Script
--  Database : careconnect_db
--  Engine   : MySQL 8.0
--  Charset  : utf8mb4
-- ============================================================

CREATE DATABASE IF NOT EXISTS careconnect_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE careconnect_db;

-- ============================================================
-- 1. USERS  (base authentication table for all roles)
-- ============================================================
CREATE TABLE IF NOT EXISTS users (
    id         BIGINT       NOT NULL AUTO_INCREMENT,
    email      VARCHAR(255) NOT NULL,
    password   VARCHAR(255) NOT NULL,
    role       ENUM('PATIENT','NURSE','ORGANIZATION') NOT NULL,
    is_active  BOOLEAN      NOT NULL DEFAULT TRUE,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_users_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 2. PATIENT PROFILES
-- ============================================================
CREATE TABLE IF NOT EXISTS patient_profiles (
    id                      BIGINT       NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT       NOT NULL,
    full_name               VARCHAR(255) NOT NULL,
    date_of_birth           DATE,
    blood_group             VARCHAR(10),
    phone                   VARCHAR(20),
    address                 TEXT,
    city                    VARCHAR(100),
    state                   VARCHAR(100),
    emergency_contact       VARCHAR(255),
    emergency_contact_phone VARCHAR(20),
    medical_history         TEXT,
    allergies               TEXT,
    current_medications     TEXT,

    PRIMARY KEY (id),
    UNIQUE KEY uq_patient_user (user_id),
    CONSTRAINT fk_patient_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 3. NURSE PROFILES
-- ============================================================
CREATE TABLE IF NOT EXISTS nurse_profiles (
    id                      BIGINT        NOT NULL AUTO_INCREMENT,
    user_id                 BIGINT        NOT NULL,
    full_name               VARCHAR(255)  NOT NULL,
    license_number          VARCHAR(255),
    phone                   VARCHAR(20),
    specialization          VARCHAR(255),
    education               VARCHAR(255),
    address                 VARCHAR(255),
    experience_years        INT,
    expertise               VARCHAR(1000),
    previous_employment     TEXT,
    professional_references TEXT,
    availability            VARCHAR(100),
    rating                  DOUBLE,
    profile_status          VARCHAR(100),

    PRIMARY KEY (id),
    UNIQUE KEY uq_nurse_user    (user_id),
    UNIQUE KEY uq_nurse_license (license_number),
    CONSTRAINT fk_nurse_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 4. ORGANIZATIONS  (hospitals, clinics, nursing homes)
-- ============================================================
CREATE TABLE IF NOT EXISTS organizations (
    id             BIGINT       NOT NULL AUTO_INCREMENT,
    user_id        BIGINT       NOT NULL,
    org_name       VARCHAR(255) NOT NULL,
    org_type       VARCHAR(100),
    reg_number     VARCHAR(255),
    contact_person VARCHAR(255),
    designation    VARCHAR(255),
    phone          VARCHAR(20),
    address        TEXT,
    city           VARCHAR(100),
    state          VARCHAR(100),
    pincode        VARCHAR(10),
    website        VARCHAR(255),
    status         VARCHAR(50)  NOT NULL DEFAULT 'ACTIVE',
    created_at     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_org_user       (user_id),
    UNIQUE KEY uq_org_reg_number (reg_number),
    CONSTRAINT fk_org_user
        FOREIGN KEY (user_id) REFERENCES users(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 5. JOBS  (postings by organizations)
-- ============================================================
CREATE TABLE IF NOT EXISTS jobs (
    id              BIGINT          NOT NULL AUTO_INCREMENT,
    organization_id BIGINT          NOT NULL,
    job_title       VARCHAR(255)    NOT NULL,
    department      VARCHAR(255),
    location        VARCHAR(255),
    job_type        ENUM('TEMPORARY','PERMANENT','EMERGENCY','CONTRACT'),
    specialization  VARCHAR(255),
    salary_min      DECIMAL(10,2),
    salary_max      DECIMAL(10,2),
    shift_details   VARCHAR(255),
    patient_acuity  VARCHAR(255),
    description     TEXT,
    priority        VARCHAR(50)     NOT NULL DEFAULT 'Normal',
    deadline        DATE,
    status          ENUM('ACTIVE','CLOSED','DRAFT') NOT NULL DEFAULT 'ACTIVE',
    created_at      DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_jobs_org    (organization_id),
    KEY idx_jobs_status (status),
    CONSTRAINT fk_jobs_org
        FOREIGN KEY (organization_id) REFERENCES organizations(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 6. APPOINTMENTS  (patient books a nurse)
-- ============================================================
CREATE TABLE IF NOT EXISTS appointments (
    id               BIGINT       NOT NULL AUTO_INCREMENT,
    patient_id       BIGINT       NOT NULL,
    nurse_id         BIGINT,
    appointment_date DATETIME     NOT NULL,
    end_date         DATETIME,
    care_needs       VARCHAR(255),
    required_skills  VARCHAR(255),
    duration         VARCHAR(100),
    notes            TEXT,
    status           ENUM('PENDING','CONFIRMED','IN_PROGRESS','COMPLETED','CANCELLED')
                     NOT NULL DEFAULT 'PENDING',
    created_at       DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_appt_patient (patient_id),
    KEY idx_appt_nurse   (nurse_id),
    KEY idx_appt_status  (status),
    CONSTRAINT fk_appt_patient
        FOREIGN KEY (patient_id) REFERENCES patient_profiles(id),
    CONSTRAINT fk_appt_nurse
        FOREIGN KEY (nurse_id)   REFERENCES nurse_profiles(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 7. MEDICAL RECORDS
-- ============================================================
CREATE TABLE IF NOT EXISTS medical_records (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    patient_id  BIGINT       NOT NULL,
    uploaded_by BIGINT,
    record_type VARCHAR(100) NOT NULL,
    title       VARCHAR(255),
    description TEXT,
    file_url    VARCHAR(500),
    file_name   VARCHAR(255),
    created_at  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_medrec_patient (patient_id),
    CONSTRAINT fk_medrec_patient
        FOREIGN KEY (patient_id)  REFERENCES patient_profiles(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_medrec_uploader
        FOREIGN KEY (uploaded_by) REFERENCES users(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 8. NURSE APPLICATIONS  (nurse applies for a job)
-- ============================================================
CREATE TABLE IF NOT EXISTS nurse_applications (
    id         BIGINT NOT NULL AUTO_INCREMENT,
    nurse_id   BIGINT NOT NULL,
    job_id     BIGINT NOT NULL,
    cover_note TEXT,
    status     ENUM('PENDING','APPROVED','REJECTED') NOT NULL DEFAULT 'PENDING',
    applied_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    UNIQUE KEY uq_nurse_job_application (nurse_id, job_id),
    KEY idx_app_nurse  (nurse_id),
    KEY idx_app_job    (job_id),
    KEY idx_app_status (status),
    CONSTRAINT fk_app_nurse
        FOREIGN KEY (nurse_id) REFERENCES nurse_profiles(id)
        ON DELETE CASCADE,
    CONSTRAINT fk_app_job
        FOREIGN KEY (job_id)   REFERENCES jobs(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 9. COMPLIANCE RECORDS
-- ============================================================
CREATE TABLE IF NOT EXISTS compliance_records (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    organization_id BIGINT,
    nurse_name      VARCHAR(255),
    requirement     VARCHAR(255),
    due_date        DATE,
    notes           TEXT,
    status          ENUM('COMPLIANT','PENDING','NON_COMPLIANT') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_compliance_org    (organization_id),
    KEY idx_compliance_status (status),
    CONSTRAINT fk_compliance_org
        FOREIGN KEY (organization_id) REFERENCES organizations(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 10. CREDENTIALS  (nurse licences and certifications)
-- ============================================================
CREATE TABLE IF NOT EXISTS credentials (
    id              BIGINT       NOT NULL AUTO_INCREMENT,
    nurse_id        BIGINT       NOT NULL,
    credential_type VARCHAR(255) NOT NULL,
    issued_by       VARCHAR(255),
    issued_date     DATE,
    expiry_date     DATE,
    document_url    VARCHAR(500),
    status          ENUM('VERIFIED','PENDING','EXPIRED') NOT NULL DEFAULT 'PENDING',
    created_at      DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_cred_nurse  (nurse_id),
    KEY idx_cred_status (status),
    KEY idx_cred_expiry (expiry_date),
    CONSTRAINT fk_cred_nurse
        FOREIGN KEY (nurse_id) REFERENCES nurse_profiles(id)
        ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- 11. PAYMENTS  (nurse compensation records)
-- ============================================================
CREATE TABLE IF NOT EXISTS payments (
    id                BIGINT         NOT NULL AUTO_INCREMENT,
    nurse_id          BIGINT         NOT NULL,
    appointment_id    BIGINT,
    amount            DECIMAL(10,2)  NOT NULL,
    payment_method    VARCHAR(100),
    payment_structure VARCHAR(100),
    hours_worked      DECIMAL(5,2),
    tax_deduction     DECIMAL(10,2),
    description       TEXT,
    status            ENUM('PENDING','PROCESSED','FAILED','REFUNDED') NOT NULL DEFAULT 'PENDING',
    payment_date      DATETIME       NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    KEY idx_pay_nurse  (nurse_id),
    KEY idx_pay_appt   (appointment_id),
    KEY idx_pay_status (status),
    CONSTRAINT fk_pay_nurse
        FOREIGN KEY (nurse_id)       REFERENCES nurse_profiles(id),
    CONSTRAINT fk_pay_appointment
        FOREIGN KEY (appointment_id) REFERENCES appointments(id)
        ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;


-- ============================================================
-- VERIFICATION QUERY  (run to confirm all 11 tables exist)
-- ============================================================
SELECT
    TABLE_NAME,
    TABLE_ROWS,
    ENGINE,
    TABLE_COLLATION
FROM INFORMATION_SCHEMA.TABLES
WHERE TABLE_SCHEMA = 'careconnect_db'
  AND TABLE_TYPE   = 'BASE TABLE'
ORDER BY TABLE_NAME;
