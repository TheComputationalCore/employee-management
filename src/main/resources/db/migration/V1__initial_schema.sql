-- =========================================================
-- EMPLOYEE MANAGEMENT SYSTEM
-- Flyway V1 - Initial Schema (FINAL)
-- Target: Spring Boot + Hibernate + PostgreSQL (Neon)
-- =========================================================

SET timezone = 'UTC';

-- =========================================================
-- EMPLOYEES
-- =========================================================
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,

    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    phone      VARCHAR(50),

    department VARCHAR(255) NOT NULL,
    position   VARCHAR(255) NOT NULL,
    salary     DOUBLE PRECISION,

    status     VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',

    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

CREATE INDEX idx_employees_department ON employees(department);
CREATE INDEX idx_employees_status ON employees(status);

-- =========================================================
-- USERS (Spring Security)
-- =========================================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,

    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role     VARCHAR(50)  NOT NULL,

    employee_id BIGINT UNIQUE,

    CONSTRAINT fk_users_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE SET NULL
);

-- =========================================================
-- DEPARTMENTS
-- =========================================================
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

-- =========================================================
-- JOBS
-- =========================================================
CREATE TABLE jobs (
    id BIGSERIAL PRIMARY KEY,

    title VARCHAR(255) NOT NULL,
    department VARCHAR(255),
    employment_type VARCHAR(255),
    location VARCHAR(255),

    description TEXT,
    required_skills TEXT,
    responsibilities TEXT,
    qualifications TEXT,
    benefits TEXT,

    experience_required INTEGER,
    salary_min DOUBLE PRECISION,
    salary_max DOUBLE PRECISION,

    active BOOLEAN NOT NULL DEFAULT TRUE
);

-- =========================================================
-- APPLICATIONS
-- =========================================================
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,

    job_id BIGINT,

    full_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),

    department VARCHAR(255),
    applied_position VARCHAR(255),

    education VARCHAR(255),
    experience TEXT,
    experience_years INTEGER,

    skills TEXT,
    parsed_skills TEXT,
    missing_skills TEXT,

    ai_score INTEGER,
    ai_summary TEXT,

    resume_path VARCHAR(255),

    status VARCHAR(50),

    shortlisted_at TIMESTAMP,
    interview_scheduled_at TIMESTAMP,
    hired_at TIMESTAMP,
    rejected_at TIMESTAMP,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    status_updated_at TIMESTAMP
);

CREATE INDEX idx_applications_status ON applications(status);
CREATE INDEX idx_applications_job_id ON applications(job_id);
CREATE INDEX idx_applications_created_at ON applications(created_at);

-- =========================================================
-- CANDIDATES
-- =========================================================
CREATE TABLE candidates (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),

    job_id BIGINT,
    job_title VARCHAR(255),

    resume_path VARCHAR(255),
    stage VARCHAR(50)
);

-- =========================================================
-- CANDIDATE NOTES
-- =========================================================
CREATE TABLE candidate_notes (
    id BIGSERIAL PRIMARY KEY,

    application_id BIGINT,
    note TEXT,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255)
);

-- =========================================================
-- CANDIDATE SCORES
-- =========================================================
CREATE TABLE candidate_scores (
    id BIGSERIAL PRIMARY KEY,

    application_id BIGINT,

    skills_score DOUBLE PRECISION,
    experience_score DOUBLE PRECISION,
    resume_score DOUBLE PRECISION,
    final_score DOUBLE PRECISION
);

-- =========================================================
-- ATTENDANCE
-- =========================================================
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    attendance_date DATE NOT NULL,

    check_in TIME,
    check_out TIME,
    total_hours DOUBLE PRECISION,

    status VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),

    CONSTRAINT uk_attendance_employee_date
        UNIQUE (employee_id, attendance_date),

    CONSTRAINT fk_attendance_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_attendance_employee ON attendance(employee_id);
CREATE INDEX idx_attendance_date ON attendance(attendance_date);

-- =========================================================
-- LEAVE BALANCES
-- =========================================================
CREATE TABLE leave_balances (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    type VARCHAR(50),

    total_days INTEGER NOT NULL DEFAULT 0,
    used_days INTEGER NOT NULL DEFAULT 0,

    CONSTRAINT fk_leave_balance_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

-- =========================================================
-- LEAVE REQUESTS
-- =========================================================
CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    type VARCHAR(50),

    start_date DATE,
    end_date DATE,

    reason VARCHAR(255),
    status VARCHAR(50),

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),

    CONSTRAINT fk_leave_request_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

-- =========================================================
-- PAYROLL
-- =========================================================
CREATE TABLE payroll (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,

    pay_month VARCHAR(20),
    pay_year INTEGER,

    base_salary DOUBLE PRECISION,
    allowances DOUBLE PRECISION,
    deductions DOUBLE PRECISION,
    net_pay DOUBLE PRECISION,

    paid BOOLEAN NOT NULL DEFAULT FALSE,
    payment_date DATE,
    generated_date DATE,

    CONSTRAINT fk_payroll_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_payroll_employee ON payroll(employee_id);
CREATE INDEX idx_payroll_year_month ON payroll(pay_year, pay_month);

-- =========================================================
-- KPIS
-- =========================================================
CREATE TABLE kpis (
    id BIGSERIAL PRIMARY KEY,

    name VARCHAR(255) NOT NULL,
    description VARCHAR(255),
    weight DOUBLE PRECISION
);

-- =========================================================
-- EMPLOYEE KPIS
-- =========================================================
CREATE TABLE employee_kpis (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    kpi_id BIGINT NOT NULL,

    target_value DOUBLE PRECISION,
    achieved_self DOUBLE PRECISION,
    achieved_manager DOUBLE PRECISION,
    final_score DOUBLE PRECISION,

    CONSTRAINT fk_employee_kpi_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_employee_kpi_kpi
        FOREIGN KEY (kpi_id)
        REFERENCES kpis(id)
        ON DELETE CASCADE
);

-- =========================================================
-- PERFORMANCE REVIEWS
-- =========================================================
CREATE TABLE performance_reviews (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,
    cycle VARCHAR(255),
    review_type VARCHAR(50),
    status VARCHAR(50),

    self_rating INTEGER,
    self_comments TEXT,

    manager_rating INTEGER,
    manager_comments TEXT,

    final_score DOUBLE PRECISION,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP,

    CONSTRAINT fk_performance_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

CREATE INDEX idx_performance_employee ON performance_reviews(employee_id);
CREATE INDEX idx_performance_status ON performance_reviews(status);

-- =========================================================
-- INTERVIEWS
-- =========================================================
CREATE TABLE interviews (
    id BIGSERIAL PRIMARY KEY,

    application_id BIGINT,
    job_id BIGINT,

    candidate_name VARCHAR(255),
    interviewer_name VARCHAR(255),

    interview_type VARCHAR(255),
    location_or_link VARCHAR(255),

    interview_date DATE,
    interview_time TIME,
    interview_date_time TIMESTAMP,

    notes TEXT
);

-- =========================================================
-- OFFER LETTERS
-- =========================================================
CREATE TABLE offer_letters (
    id BIGSERIAL PRIMARY KEY,

    application_id BIGINT,

    position VARCHAR(255),
    salary DOUBLE PRECISION,
    joining_date DATE,

    file_path VARCHAR(255)
);

-- =========================================================
-- ONBOARDING FLOWS
-- =========================================================
CREATE TABLE onboarding_flows (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT NOT NULL,

    created_at DATE DEFAULT CURRENT_DATE,
    completed_at DATE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,

    CONSTRAINT fk_onboarding_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
        ON DELETE CASCADE
);

-- =========================================================
-- ONBOARDING TEMPLATE TASKS
-- =========================================================
CREATE TABLE onboarding_template_tasks (
    id BIGSERIAL PRIMARY KEY,

    title VARCHAR(255),
    description TEXT,
    required BOOLEAN NOT NULL DEFAULT TRUE,
    sort_order INTEGER
);

-- =========================================================
-- ONBOARDING TASKS
-- =========================================================
CREATE TABLE onboarding_tasks (
    id BIGSERIAL PRIMARY KEY,

    employee_id BIGINT,
    flow_id BIGINT,

    title VARCHAR(255) NOT NULL,
    description TEXT,

    assigned_to VARCHAR(255) NOT NULL,
    due_date DATE,

    required BOOLEAN NOT NULL DEFAULT TRUE,
    completed BOOLEAN NOT NULL DEFAULT FALSE,

    file_path VARCHAR(255),

    CONSTRAINT fk_onboarding_task_flow
        FOREIGN KEY (flow_id)
        REFERENCES onboarding_flows(id)
        ON DELETE CASCADE
);

-- =========================================================
-- EMAIL LOGS
-- =========================================================
CREATE TABLE email_logs (
    id BIGSERIAL PRIMARY KEY,

    to_email VARCHAR(255),
    subject VARCHAR(255),
    body TEXT,
    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
