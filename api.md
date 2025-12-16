# Employee Management System – API Documentation

## Overview
This document describes the REST APIs exposed by the **Employee Management System (EMS)**.
The application supports **Admin**, **HR**, and **Employee** roles and is deployed on **Render**
with **Neon PostgreSQL**.

- Base URL (Production): https://employee-management-qhfh.onrender.com
- Authentication: Session-based (Spring Security form login)
- API Docs (Swagger): `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

---

## Authentication

### Login
**POST** `/do-login`

Form parameters:
- `username`
- `password`

On success → redirects to `/web/dashboard`

### Logout
**POST** `/logout`

---

## Roles

| Role | Description |
|----|----|
| ROLE_ADMIN | Full system access |
| ROLE_HR | HR, recruitment, payroll, onboarding |
| ROLE_EMPLOYEE | Self-service (attendance, payroll, performance) |

---

## Dashboard

### Get Dashboard Summary
**GET** `/web/dashboard`  
Roles: ADMIN, HR

Returns dashboard statistics:
- Total employees
- Departments
- Payroll & onboarding summaries

---

## Employee Management

### List Employees
**GET** `/web/employees`

Query params:
- `page`
- `size`
- `search`
- `department`
- `sort`

Roles: ADMIN, HR

### Add Employee
**POST** `/web/employees/add`  
Roles: ADMIN, HR

### Edit Employee
**POST** `/web/employees/edit/{id}`  
Roles: ADMIN, HR

### Soft Delete Employee
**GET** `/web/employees/delete/{id}`  
Roles: ADMIN, HR

### Restore Employee
**GET** `/web/employees/restore/{id}`  
Roles: ADMIN, HR

---

## Attendance

### My Attendance
**GET** `/web/attendance/my`  
Role: EMPLOYEE

### Clock In
**POST** `/web/attendance/clock-in`  
Role: EMPLOYEE

### Clock Out
**POST** `/web/attendance/clock-out`  
Role: EMPLOYEE

### View All Attendance
**GET** `/web/attendance`  
Roles: ADMIN, HR

---

## Recruitment & Careers

### Public Job Listings
**GET** `/careers`

### Job Details
**GET** `/careers/job/{id}`

### Apply for Job
**POST** `/careers/apply/{jobId}`  
Multipart:
- resume (PDF/DOC/DOCX)

AI-powered resume parsing & scoring is triggered automatically.

---

## Recruitment (Internal)

### Jobs
- **GET** `/web/recruitment/jobs`
- **POST** `/web/recruitment/jobs/add`
- **POST** `/web/recruitment/jobs/edit/{id}`
- **GET** `/web/recruitment/jobs/close/{id}`

Roles: ADMIN, HR

### Applications by Job
**GET** `/web/recruitment/applications/{jobId}`

### Candidate Profile
**GET** `/web/recruitment/candidate/{appId}`

### Smart Shortlist (AI)
**GET** `/web/recruitment/smart-shortlist/{jobId}`

---

## Interviews

### Schedule Interview
**POST** `/web/recruitment/interview/schedule/{appId}`  
Roles: ADMIN, HR

---

## Offer Letters

### Generate Offer
**POST** `/web/recruitment/offer/{appId}`

### Download Offer PDF
**GET** `/web/recruitment/offer/download/{appId}`

---

## Onboarding

### Start Onboarding (Auto on Hire)
Triggered internally when application status = `Hired`

### View Onboarding Dashboard
**GET** `/web/onboarding`

---

## Payroll

### My Payroll
**GET** `/web/payroll/my`  
Role: EMPLOYEE

### All Payrolls
**GET** `/web/payroll`  
Roles: ADMIN, HR

### Generate Payroll
**POST** `/web/payroll/generate`  
Roles: ADMIN, HR

---

## Performance Reviews

### List Reviews
**GET** `/web/performance`  
Roles: ADMIN, HR

### My Reviews
**GET** `/web/performance/my`  
Role: EMPLOYEE

### Submit Self Review
**POST** `/web/performance/self/{id}`  
Role: EMPLOYEE

### Manager Review
**POST** `/web/performance/manager/{id}`  
Roles: ADMIN, HR

---

## File Downloads

### Resume Download
**GET** `/web/recruitment/resume/{appId}`

---

## Error Handling

- 401 → Unauthorized
- 403 → Forbidden
- 404 → Resource not found
- 500 → Internal server error

---

## Notes

- CSRF protection enabled (Cookie-based)
- File uploads stored outside Git tracking
- AI resume scoring is deterministic & rule-based
- Flyway enabled in DEV, disabled in PROD
