
# 🏢 Employee Management System (EMS)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://adoptium.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Neon-blue.svg)](https://neon.tech)
[![Docker](https://img.shields.io/badge/Docker-Enabled-blue.svg)](https://www.docker.com/)
[![CI](https://github.com/TheComputationalCore/employee-management/actions/workflows/ci.yml/badge.svg)](https://github.com/TheComputationalCore/employee-management/actions)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)

**Live Deployment:** 🌐 https://employee-management-qhfh.onrender.com

---

## 📌 Overview

The **Employee Management System (EMS)** is a **production-grade, enterprise-ready HRMS platform** built using **Spring Boot**, **PostgreSQL (Neon)**, **Docker**, and **Thymeleaf**.

It covers the **entire employee lifecycle**, from recruitment and AI-powered resume screening to onboarding, attendance, payroll, leave management, KPIs, and performance reviews — all with **role-based access control**.

---

## 👥 User Roles & Access Control

| Role | Capabilities |
|----|----|
| **Admin** | Full system access, analytics, payroll |
| **HR** | Recruitment, interviews, performance |
| **Employee** | Attendance, leave, payroll, reviews |

---

## ✨ Core Features

- 🔐 Secure Authentication (Spring Security + BCrypt)
- 🧠 AI Resume Parsing & Scoring
- 📊 Analytics Dashboards
- 👔 Employee Lifecycle Management
- 🌴 Leave & Attendance Tracking
- 🚀 Onboarding Automation
- 💰 Payroll Processing
- 📈 KPI & Performance Reviews

---

## 📸 Screenshots

![Login](docs/screenshots/login.png)
![Dashboard](docs/screenshots/dashboard1.png)
![AI Score](docs/screenshots/ai_score_breakdown.png)

---

## 📐 Architecture

```mermaid
graph TD
UI --> API --> SERVICE --> DB
SERVICE --> AI
SERVICE --> FILES
```

---

## 🐳 Docker

```bash
docker-compose up --build
```

---

## 📚 API Docs

See **api.md**

---

## 🚀 Deployment

**Live URL:** https://employee-management-qhfh.onrender.com

---

## 📜 License

MIT License
