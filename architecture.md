
# 🏗️ System Architecture — Employee Management Platform

This document provides a **deep, production-grade architectural overview** of the Employee Management System (EMS).  
It explains **how the system is designed**, **why certain decisions were made**, and **how components interact** — suitable for **senior engineers, reviewers, and interview discussions**.

---

## 1️⃣ High-Level Architecture

The system follows a **layered, modular monolithic architecture** with clear separation of concerns.

```mermaid
flowchart TB
    User[Browser / Client]
    LB[Render Load Balancer]
    App[Spring Boot Application]
    Sec[Spring Security]
    Web[Web Controllers]
    Service[Service Layer]
    Repo[Repository Layer]
    DB[(PostgreSQL - Neon)]
    FS[(File Storage)]
    AI[AI Resume Scoring Engine]

    User --> LB --> App
    App --> Sec
    Sec --> Web
    Web --> Service
    Service --> Repo
    Repo --> DB
    Service --> FS
    Service --> AI
```

### Key Characteristics
- **Monolithic but modular** (scales well for startups & mid-size orgs)
- **Stateless backend** (session via Spring Security)
- **Cloud-native ready**
- **Strong domain separation**

---

## 2️⃣ Architectural Layers Explained

### 🔐 Security Layer
- **Spring Security 6**
- Form-based authentication
- Role-based access control:
  - `ROLE_ADMIN`
  - `ROLE_HR`
  - `ROLE_EMPLOYEE`
- CSRF protection enabled
- Passwords encrypted using **BCrypt**

```text
SecurityConfig
 └── AuthenticationProvider (DAO)
 └── CustomUserDetailsService
 └── Role-based route protection
```

---

### 🌐 Web Layer (Controllers)
Handles **HTTP requests**, view rendering, and validation.

Responsibilities:
- Request routing
- Input validation
- Role-based access control
- Model preparation for Thymeleaf

Examples:
- `EmployeeWebController`
- `RecruitmentController`
- `PayrollWebController`
- `PerformanceWebController`

> Controllers never contain business logic — they delegate to services.

---

### 🧠 Service Layer (Business Logic)

This is the **heart of the application**.

Responsibilities:
- Enforces business rules
- Coordinates multiple repositories
- Orchestrates workflows
- Handles transactions

Examples:
- `EmployeeServiceImpl`
- `RecruitmentServiceImpl`
- `PayrollServiceImpl`
- `PerformanceServiceImpl`
- `OnboardingServiceImpl`

```text
Controller
  ↓
Service (Transactional)
  ↓
Repository
```

✔ Each service is:
- Unit-testable
- Stateless
- Focused on a single domain

---

### 🗄️ Repository Layer (Data Access)

Implemented using **Spring Data JPA**.

Responsibilities:
- Abstract database access
- Custom queries for analytics & search
- Pagination & sorting

Examples:
- `EmployeeRepository`
- `ApplicationRepository`
- `AttendanceRepository`
- `PayrollRepository`

Database:
- **PostgreSQL (Neon)** in production
- **H2 (in-memory)** for development

---

## 3️⃣ Database Architecture

### Key Design Principles
- Normalized schema
- Audit fields via `BaseEntity`
- Soft deletes (Employee)
- Enum-driven states

```mermaid
erDiagram
    EMPLOYEE ||--o{ ATTENDANCE : has
    EMPLOYEE ||--o{ PAYROLL : receives
    EMPLOYEE ||--o{ LEAVE_REQUEST : submits
    EMPLOYEE ||--o{ PERFORMANCE_REVIEW : evaluated_in

    JOB ||--o{ APPLICATION : receives
    APPLICATION ||--o{ INTERVIEW : schedules
    APPLICATION ||--o{ OFFER_LETTER : generates

    EMPLOYEE ||--|| USER : authenticates_as
```

---

## 4️⃣ File Storage Architecture

Files are **stored outside the database** for scalability.

```text
uploads/
 ├── resumes/
 ├── offers/
 └── onboarding/
```

Usage:
- Resume uploads
- Offer letter PDFs
- Onboarding documents

✔ Stored paths are persisted in DB  
✔ Secure access via controller-level checks

---

## 5️⃣ AI Resume Scoring Engine

A lightweight, explainable AI engine.

### Inputs
- Resume text (PDF parsing)
- Job required skills
- Experience & education

### Outputs
- AI Score (0–100)
- Missing skills
- Human-readable summary

```mermaid
sequenceDiagram
    Candidate->>System: Upload Resume
    System->>ResumeParser: Extract Text
    ResumeParser->>AIEngine: Parsed Data
    AIEngine->>System: Score + Summary
    System->>DB: Persist Results
```

✔ Transparent scoring (no black box)
✔ Deterministic & auditable

---

## 6️⃣ Workflow Architecture (End-to-End)

### Recruitment → Employee Lifecycle

```mermaid
stateDiagram-v2
    Applied --> Shortlisted
    Shortlisted --> Interviewing
    Interviewing --> Hired
    Interviewing --> Rejected
    Hired --> Onboarding
    Onboarding --> ActiveEmployee
```

Once hired:
- Employee record auto-created
- User account generated
- Onboarding flow initiated

---

## 7️⃣ CI/CD Architecture

### Continuous Integration
- GitHub Actions
- Java 17 & 21 matrix
- Maven build & test
- Artifact uploads

```mermaid
flowchart LR
    Code[Git Push]
    CI[GitHub Actions]
    Build[Maven Build]
    Test[JUnit Tests]
    Artifact[JAR Artifact]

    Code --> CI --> Build --> Test --> Artifact
```

---

## 8️⃣ Deployment Architecture

### Production Stack
- **Render** — App hosting
- **Neon PostgreSQL** — Managed DB
- **Docker** — Containerized builds

```mermaid
flowchart TB
    User --> Render
    Render --> SpringBoot
    SpringBoot --> NeonDB
```

Environment separation:
- `dev` → H2 + seed data
- `prod` → PostgreSQL (Neon)

---

## 9️⃣ Scalability & Future Enhancements

Designed to scale into:
- Microservices (if needed)
- Event-driven workflows
- External AI services
- Object storage (S3-compatible)

Planned upgrades:
- Redis caching
- Async job processing
- OAuth2 / SSO
- GraphQL API layer

---

## 🔚 Summary

This architecture:
- Is **production-ready**
- Balances simplicity with power
- Demonstrates **real-world engineering maturity**
- Is ideal for **enterprise-grade HR platforms**

