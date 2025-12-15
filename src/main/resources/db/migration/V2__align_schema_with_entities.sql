-- =========================================================
-- Flyway V2
-- Align schema with JPA entities (Spring Boot 4)
-- SAFE: additive + type corrections only
-- =========================================================

/* =========================================================
   APPLICATIONS
   ========================================================= */

-- Fix DATE vs TIMESTAMP mismatch
ALTER TABLE applications
    ALTER COLUMN created_at TYPE DATE
    USING created_at::DATE;

ALTER TABLE applications
    ALTER COLUMN status_updated_at TYPE DATE
    USING status_updated_at::DATE;


/* =========================================================
   INTERVIEWS
   ========================================================= */

-- Required by Interview entity & services
ALTER TABLE interviews
    ADD COLUMN IF NOT EXISTS type VARCHAR(255);

ALTER TABLE interviews
    ADD COLUMN IF NOT EXISTS date DATE;

ALTER TABLE interviews
    ADD COLUMN IF NOT EXISTS time TIME;

ALTER TABLE interviews
    ADD COLUMN IF NOT EXISTS interview_scheduled_at TIMESTAMP;


/* =========================================================
   ATTENDANCE
   ========================================================= */

-- Improve analytics & auditing performance
CREATE INDEX IF NOT EXISTS idx_attendance_employee_id
    ON attendance(employee_id);

CREATE INDEX IF NOT EXISTS idx_attendance_date
    ON attendance(date);


/* =========================================================
   LEAVE BALANCES
   ========================================================= */

-- Enforce entity defaults
ALTER TABLE leave_balances
    ALTER COLUMN total_days SET DEFAULT 0;

ALTER TABLE leave_balances
    ALTER COLUMN used_days SET DEFAULT 0;

UPDATE leave_balances
SET total_days = 0
WHERE total_days IS NULL;

UPDATE leave_balances
SET used_days = 0
WHERE used_days IS NULL;


/* =========================================================
   PERFORMANCE REVIEWS
   ========================================================= */

CREATE INDEX IF NOT EXISTS idx_performance_employee
    ON performance_reviews(employee_id);

CREATE INDEX IF NOT EXISTS idx_performance_status
    ON performance_reviews(status);


/* =========================================================
   APPLICATIONS ANALYTICS
   ========================================================= */

CREATE INDEX IF NOT EXISTS idx_applications_status
    ON applications(status);

CREATE INDEX IF NOT EXISTS idx_applications_job_id
    ON applications(job_id);

CREATE INDEX IF NOT EXISTS idx_applications_created_at
    ON applications(created_at);


/* =========================================================
   PAYROLL
   ========================================================= */

CREATE INDEX IF NOT EXISTS idx_payroll_employee
    ON payroll(employee_id);

CREATE INDEX IF NOT EXISTS idx_payroll_year_month
    ON payroll(year, month);


/* =========================================================
   SAFETY CHECK
   ========================================================= */

-- No drops, no renames, no destructive ops
