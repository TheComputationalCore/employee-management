# 🏢 Employee Management System (EMS)

A **production-grade, enterprise-ready Employee Management System** built with **Spring Boot 4**, designed to handle the **complete employee lifecycle** — from recruitment and AI-powered resume screening to payroll, performance reviews, attendance, leave management, and onboarding.

This project demonstrates **real-world architecture**, **secure role-based access**, **AI-assisted recruitment**, and **cloud-native deployment** using **Docker, Render, and Neon PostgreSQL**.

---

## 🌐 Live Deployment

🔗 **Production URL**:  
https://employee-management-qhfh.onrender.com

> Deployed on **Render** with **Neon Serverless PostgreSQL**

---

## 🎯 Key Highlights

✔ Enterprise-grade HRMS architecture  
✔ Role-based access: **Admin / HR / Employee**  
✔ AI-powered resume parsing & scoring  
✔ End-to-end recruitment pipeline  
✔ Payroll generation & export  
✔ Performance reviews (Self + Manager)  
✔ Attendance & leave management  
✔ Secure authentication (Spring Security)  
✔ CI/CD with GitHub Actions  
✔ Dockerized & cloud-ready  

---

## 🧑‍💼 User Roles & Capabilities

### 👑 Admin
- Full system access
- Manage employees & departments
- Recruitment pipeline oversight
- Payroll generation & approvals
- Performance cycle creation
- Dashboard & analytics

### 🧑‍💻 HR
- Employee & department management
- Recruitment & interviews
- AI-based candidate shortlisting
- Onboarding workflows
- Leave approvals
- Payroll operations

### 👤 Employee
- View dashboard
- Clock-in / clock-out attendance
- Apply for leave
- View payroll slips
- Self performance reviews
- Complete onboarding tasks

---

## 🧠 AI-Powered Recruitment

The system includes **resume intelligence** features:

- 📄 Resume upload (PDF/DOCX)
- 🔍 Skill extraction
- 🧮 Experience & education parsing
- 🤖 AI match scoring (0–100)
- ❌ Missing skill detection
- ⚡ Smart auto-shortlisting
- 📝 AI-generated candidate summaries

This enables **data-driven hiring decisions**.

---

## 🧩 Core Modules

| Module | Description |
|------|------------|
| Dashboard | Analytics & KPIs |
| Employee | CRUD, search, soft delete |
| Attendance | Clock-in / clock-out |
| Leave | Apply, approve, analytics |
| Payroll | Generate, export, mark paid |
| Recruitment | Jobs, applications, interviews |
| Performance | Self & manager reviews |
| Onboarding | Tasks & templates |
| AI Engine | Resume scoring & insights |
| Security | Auth, roles, CSRF |
| Audit | CreatedBy / UpdatedBy tracking |

---

## 🛠 Tech Stack

### Backend
- Java 17
- Spring Boot 4
- Spring MVC
- Spring Data JPA
- Spring Security (Session + CSRF)
- MapStruct
- Lombok

### Frontend
- Thymeleaf
- Thymeleaf Layout Dialect
- HTML / CSS

### Database
- **Neon PostgreSQL (Production)**
- H2 (Dev)

### DevOps
- Docker
- GitHub Actions (CI)
- Render (Deployment)

### Docs & Utilities
- Swagger / OpenAPI
- PDFBox
- OpenPDF
- Apache POI

---

## 🔐 Security Overview

- Session-based authentication
- Cookie-based CSRF protection
- Role-based authorization
- Secure password hashing (BCrypt)
- Auditing (createdBy / updatedBy)
- Dev-only data seeding

---

## 🧪 Default Dev Credentials (DEV profile only)

| Username | Role | Password |
|--------|------|----------|
| admin | ADMIN | employee123 |
| hr | HR | employee123 |
| employee | EMPLOYEE | employee123 |

> ⚠️ **Disabled in production**

---

## ⚙️ Configuration & Profiles

### Profiles
- `dev` → H2 + Flyway
- `prod` → Neon PostgreSQL (no Flyway)

Set profile via environment variable:

```bash
SPRING_PROFILES_ACTIVE=dev
```

---

## 🐳 Docker

### Build
```bash
docker build -t employee-management .
```

### Run
```bash
docker run -p 8080:8080 employee-management
```

---

## 🔄 CI/CD

- Automated build via **GitHub Actions**
- Docker image build
- Production deployment on Render

Workflow location:
```
.github/workflows/
```

---

## 📘 API Documentation

- **Detailed API docs**:  
  👉 [api.md](./api.md)

- Swagger UI (runtime):
  - `/swagger-ui.html`
  - `/v3/api-docs`

---

## 🗂 Repository Structure

```
employee-management/
├── README.md
├── api.md
├── pom.xml
├── Dockerfile
├── .github/workflows/
├── src/
│   └── main/
│       ├── java/com/empmgmt/
│       ├── resources/
│       └── templates/
└── target/
```

---

## 📸 Screenshots

Screenshots are stored separately for documentation purposes and are **not part of runtime uploads**.

Recommended structure:
```
docs/images/
```

---

## 📜 License

This project is licensed under the **MIT License**.

See: [LICENSE](./LICENSE)

---

## 🚀 Why This Project Stands Out

- Not a tutorial — a **real HRMS**
- Covers **full employee lifecycle**
- Demonstrates **secure, scalable design**
- Includes **AI-driven features**
- Cloud-deployed & production-ready

Perfect for:
- 💼 Portfolio projects
- 🧑‍💻 Backend engineering roles
- 🏢 Enterprise application demos

---

## 🤝 Contributing

Pull requests are welcome.  
For major changes, please open an issue first to discuss proposed updates.

---

## ⭐ Final Note

This project reflects **real-world engineering practices**, not just framework usage.

If you find it useful, consider giving it a ⭐ on GitHub.
