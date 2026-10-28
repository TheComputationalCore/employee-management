# 📘 Employee Management System – API Documentation

This document summarizes the application's current browser routes. It is a portfolio/demo project and has not been independently security- or production-validated.

The system is built using **Spring Boot**, secured with **Spring Security (Session + CSRF)**, and deployed on **Render** with **Neon PostgreSQL**.

---

## 🔐 Authentication & Security

- Authentication: **Form Login (Session-based)**
- CSRF Protection: **Enabled (Cookie-based CSRF tokens)**
- Roles:
  - `ROLE_ADMIN`
  - `ROLE_HR`
  - `ROLE_EMPLOYEE`

> Most APIs require authentication and appropriate role access.

Employee account setup uses a single-use code issued by HR at `POST /web/accounts/setup-code/{employeeId}` and entered at `POST /account/setup`. No email is sent; the code expires after 24 hours.

---

## 🧑‍ Roles & Access Matrix

| Feature | Admin | HR | Employee |
|------|------|------|----------|
| Dashboard | ✅ | ✅ | ✅ |
| Employee Management | ✅ | ✅ | ❌ |
| Recruitment | ✅ | ✅ | ❌ |
| Attendance | ✅ | ✅ | ✅ |
| Payroll | ✅ | ✅ | Employee-only |
| Performance Reviews | ✅ | ✅ | Self-review |
| Leave Management | ✅ | ✅ | Apply |
| Onboarding | ✅ | ✅ | Assigned tasks |

---

## 🌐 Base URLs

| Environment | Base URL |
|-----------|---------|
| Local | `http://localhost:8080` |
| Production | `https://employee-management-qhfh.onrender.com` |

---

## 📊 Dashboard APIs

### Get Dashboard Summary
**GET** `/web/dashboard`

Returns aggregated metrics:
- Total employees
- Departments
- Payroll stats
- Onboarding overview

_Response:_
```json
{
  "totalEmployees": 42,
  "totalDepartments": 6,
  "averageSalary": 72000
}
```

---

## 👤 Employee APIs

### List Employees (Admin / HR)
**GET** `/web/employees`

Query Params:
- `page`
- `size`
- `sort`
- `search`
- `department`

### Create Employee
**POST** `/web/employees/add`

### Update Employee
**POST** `/web/employees/edit/{id}`

### Soft Delete Employee
**GET** `/web/employees/delete/{id}`

### Restore Employee
**GET** `/web/employees/restore/{id}`

---

## ⏱ Attendance APIs

### Clock In
**POST** `/web/attendance/clock-in`

### Clock Out
**POST** `/web/attendance/clock-out`

### My Attendance
**GET** `/web/attendance/my`

### All Attendance (Admin / HR)
**GET** `/web/attendance`

---

## 🏖 Leave Management APIs

### Apply Leave
**POST** `/web/leave/apply`

### Approve / Reject Leave
**POST** `/web/leave/update/{id}`

### Leave Analytics
**GET** `/web/leave/analytics`

---

## 💰 Payroll APIs

### Generate Payroll (Admin / HR)
**POST** `/web/payroll/generate`

### My Payroll (Employee)
**GET** `/web/payroll/my`

### Mark Payroll as Paid
**POST** `/web/payroll/mark-paid/{id}`

---

## ⭐ Performance Review APIs

### Create Review Cycle
**POST** `/web/performance/create`

### Submit Self Review
**POST** `/web/performance/self/{id}`

### Manager Review
**POST** `/web/performance/manager/{id}`

### List Reviews
**GET** `/web/performance`

---

## 🧠 Recruitment & AI APIs

### List Jobs
**GET** `/careers`

### Apply for Job
**POST** `/careers/apply/{jobId}`

Accepts:
- Resume (PDF only, up to 5 MiB)
- Candidate details

Resume processing (deterministic heuristics; no AI model):
- Skill extraction
- Experience parsing
- Education detection
- Match score (0–100)
- Missing skill analysis

### Smart Shortlist
**POST** `/web/recruitment/smart-shortlist/{jobId}`

### Candidate Profile
**GET** `/web/recruitment/candidate/{appId}`

---

## 📄 Offer Letter APIs

### Generate Offer Letter
**POST** `/web/recruitment/offer/{appId}`

### Download Offer PDF
**GET** `/web/recruitment/offer/download/{appId}`

---

## 🧾 Onboarding APIs

### Start Onboarding
**POST** `/web/onboarding/start/{employeeId}`

### My Onboarding Tasks
**GET** `/web/onboarding/my`

### Complete Task
**POST** `/web/onboarding/task/{id}/complete`

---

## 📂 File Handling

| File Type | Location |
|---------|----------|
| Resumes | `uploads/resumes/` |
| Offer Letters | `uploads/offers/` |
| Onboarding Docs | `uploads/onboarding/` |

> Uploads directory is excluded from Git and generated at runtime. Configure `APP_UPLOAD_DIR` to point to durable storage in deployments that need files to survive restarts.

---

## 📖 Swagger / OpenAPI

Available when application is running:

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

---

## ⚙️ Error Handling

Standard HTTP Status Codes:
- `200 OK`
- `400 Bad Request`
- `401 Unauthorized`
- `403 Forbidden`
- `404 Not Found`
- `500 Internal Server Error`

---

## 🧪 Testing & CI

- CI via **GitHub Actions** (Maven verification and CodeQL)
- Dockerized builds

The repository currently has no automated test sources, so CI does not provide behavioral test coverage yet.

---

## 🏁 Notes

- APIs are **UI-backed (Thymeleaf)** and **REST-backed**
- Security enforced via **Spring Security**
- Database: **Neon PostgreSQL**
- Portfolio/demo architecture; production readiness has not been independently validated

---

📌 _This document describes the intended route surface. Confirm individual paths and response types against the controllers before integrating; `/web/**` routes generally render Thymeleaf views, while JSON endpoints are under `/api/v1/**`._
