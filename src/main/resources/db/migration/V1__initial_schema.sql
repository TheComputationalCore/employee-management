-- ================================
-- EMPLOYEE MANAGEMENT SYSTEM
-- Initial PostgreSQL Schema
-- Generated for Flyway
-- ================================

-- ================================
-- EMPLOYEES
-- ================================
CREATE TABLE employees (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    phone VARCHAR(255),
    department VARCHAR(255) NOT NULL,
    position VARCHAR(255) NOT NULL,
    salary DOUBLE PRECISION,
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255),
    updated_by VARCHAR(255)
);

CREATE INDEX idx_employee_email ON employees(email);

-- ================================
-- USERS
-- ================================
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255),
    password VARCHAR(255),
    role VARCHAR(50),
    employee_id BIGINT UNIQUE,
    CONSTRAINT fk_users_employee
        FOREIGN KEY (employee_id)
        REFERENCES employees(id)
);

-- ================================
-- DEPARTMENTS
-- ================================
CREATE TABLE departments (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE,
    description VARCHAR(255),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

-- ================================
-- JOBS
-- ================================
CREATE TABLE jobs (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    department VARCHAR(255),
    description TEXT,
    responsibilities TEXT,
    required_skills TEXT,
    qualifications TEXT,
    benefits TEXT,
    employment_type VARCHAR(255),
    experience_required INTEGER,
    salary_min DOUBLE PRECISION,
    salary_max DOUBLE PRECISION,
    location VARCHAR(255),
    active BOOLEAN
);

-- ================================
-- APPLICATIONS
-- ================================
CREATE TABLE applications (
    id BIGSERIAL PRIMARY KEY,
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
    status VARCHAR(255),
    status_updated_at DATE,
    interview_scheduled_at TIMESTAMP,
    shortlisted_at TIMESTAMP,
    hired_at TIMESTAMP,
    rejected_at TIMESTAMP,
    job_id BIGINT,
    created_at DATE
);

-- ================================
-- CANDIDATES
-- ================================
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

-- ================================
-- CANDIDATE NOTES
-- ================================
CREATE TABLE candidate_notes (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT,
    note TEXT,
    created_at TIMESTAMP,
    created_by VARCHAR(255)
);

-- ================================
-- CANDIDATE SCORES
-- ================================
CREATE TABLE candidate_scores (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT,
    experience_score DOUBLE PRECISION NOT NULL,
    resume_score DOUBLE PRECISION NOT NULL,
    skills_score DOUBLE PRECISION NOT NULL,
    final_score DOUBLE PRECISION NOT NULL
);

-- ================================
-- ATTENDANCE
-- ================================
CREATE TABLE attendance (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    date DATE,
    check_in TIME,
    check_out TIME,
    total_hours DOUBLE PRECISION,
    status VARCHAR(50),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255),
    CONSTRAINT uk_attendance UNIQUE (employee_id, date)
);

-- ================================
-- LEAVE BALANCES
-- ================================
CREATE TABLE leave_balances (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    type VARCHAR(50),
    total_days INTEGER,
    used_days INTEGER
);

-- ================================
-- LEAVE REQUESTS
-- ================================
CREATE TABLE leave_requests (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    start_date DATE,
    end_date DATE,
    reason VARCHAR(255),
    type VARCHAR(50),
    status VARCHAR(50),
    created_at TIMESTAMP,
    created_by VARCHAR(255),
    updated_at TIMESTAMP,
    updated_by VARCHAR(255)
);

-- ================================
-- PAYROLL
-- ================================
CREATE TABLE payroll (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    month VARCHAR(255),
    year INTEGER NOT NULL,
    base_salary DOUBLE PRECISION,
    allowances DOUBLE PRECISION,
    deductions DOUBLE PRECISION,
    net_pay DOUBLE PRECISION,
    paid BOOLEAN NOT NULL,
    payment_date DATE,
    generated_date DATE
);

-- ================================
-- KPIS
-- ================================
CREATE TABLE kpis (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    description VARCHAR(255),
    weight DOUBLE PRECISION
);

-- ================================
-- EMPLOYEE KPIS
-- ================================
CREATE TABLE employee_kpis (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    kpi_id BIGINT,
    target_value DOUBLE PRECISION,
    achieved_self DOUBLE PRECISION,
    achieved_manager DOUBLE PRECISION,
    final_score DOUBLE PRECISION
);

-- ================================
-- PERFORMANCE REVIEWS
-- ================================
CREATE TABLE performance_reviews (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    review_type VARCHAR(50),
    cycle VARCHAR(255),
    self_rating INTEGER,
    manager_rating INTEGER,
    final_score DOUBLE PRECISION,
    self_comments TEXT,
    manager_comments TEXT,
    status VARCHAR(50),
    created_at TIMESTAMP,
    updated_at TIMESTAMP
);

-- ================================
-- INTERVIEWS
-- ================================
CREATE TABLE interviews (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT,
    candidate_name VARCHAR(255),
    interviewer_name VARCHAR(255),
    interview_type VARCHAR(255),
    location_or_link VARCHAR(255),
    interview_date_time TIMESTAMP,
    notes TEXT,
    job_id BIGINT
);

-- ================================
-- OFFER LETTERS
-- ================================
CREATE TABLE offer_letters (
    id BIGSERIAL PRIMARY KEY,
    application_id BIGINT,
    position VARCHAR(255),
    salary DOUBLE PRECISION,
    joining_date DATE,
    file_path VARCHAR(255)
);

-- ================================
-- ONBOARDING FLOWS
-- ================================
CREATE TABLE onboarding_flows (
    id BIGSERIAL PRIMARY KEY,
    employee_id BIGINT,
    completed BOOLEAN NOT NULL,
    created_at DATE,
    completed_at DATE
);

-- ================================
-- ONBOARDING TEMPLATE TASKS
-- ================================
CREATE TABLE onboarding_template_tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255),
    description TEXT,
    required BOOLEAN NOT NULL,
    sort_order INTEGER
);

-- ================================
-- ONBOARDING TASKS
-- ================================
CREATE TABLE onboarding_tasks (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    assigned_to VARCHAR(255) NOT NULL,
    due_date DATE,
    completed BOOLEAN NOT NULL,
    required BOOLEAN NOT NULL,
    employee_id BIGINT,
    flow_id BIGINT,
    file_path VARCHAR(255),
    CONSTRAINT fk_onboarding_flow
        FOREIGN KEY (flow_id)
        REFERENCES onboarding_flows(id)
);

-- ================================
-- EMAIL LOGS
-- ================================
CREATE TABLE email_logs (
    id BIGSERIAL PRIMARY KEY,
    to_email VARCHAR(255),
    subject VARCHAR(255),
    body TEXT,
    timestamp TIMESTAMP
);
