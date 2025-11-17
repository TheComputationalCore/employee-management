# 🚀 Employee Management System

A complete **Employee Management System** built with **Spring Boot 3**, **Spring Data JPA**, **Thymeleaf**, **Bootstrap 5**, and **MySQL**.  
Supports full **CRUD operations**, form validation, REST APIs, and Swagger documentation. Ideal as a portfolio demo for full-stack Java roles.

---

## 📺 Demo (short walkthrough)
Watch the demo: https://youtu.be/TXTjFwRVCm0

---

## 🔥 Highlights
- Full CRUD: Create / Read / Update / Delete employee records  
- Responsive UI with **Bootstrap 5**  
- REST API endpoints (Swagger/OpenAPI)  
- Form validation (client + server)  
- MySQL database integration via JPA  
- Clean package structure and maintainable code  

---

## 🧩 Tech Stack
**Backend:** Spring Boot 3, Spring Data JPA  
**Frontend:** Thymeleaf, Bootstrap 5, jQuery  
**Database:** MySQL  
**Build Tool:** Maven  
**API Docs:** Swagger/OpenAPI  

---

## 📁 Project Structure
```
employee-management/
├── src/
│   ├── main/java/.../controller
│   ├── main/java/.../service
│   ├── main/java/.../repository
│   ├── main/java/.../model
│   └── main/resources/
│       ├── templates/
│       └── static/
├── screenshots/
└── README.md
```

---

## ⚙️ Quickstart

### 1️⃣ Clone
```bash
git clone https://github.com/TheComputationalCore/employee-management.git
cd employee-management
```

### 2️⃣ Create MySQL database
```sql
CREATE DATABASE employee_management;
```

### 3️⃣ Configure credentials  
Update:

`src/main/resources/application.properties`  
(or use a separate `application-local.properties`)

### 4️⃣ Build and run
```bash
mvn clean install
mvn spring-boot:run
```

### 5️⃣ Access  
- Web UI → http://localhost:8080  
- Swagger Docs → http://localhost:8080/swagger-ui.html  

---

## 📄 REST API Summary
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET    | `/api/employees` | Get all employees |
| GET    | `/api/employees/{id}` | Get by ID |
| POST   | `/api/employees` | Create |
| PUT    | `/api/employees/{id}` | Update |
| DELETE | `/api/employees/{id}` | Delete |
| GET    | `/api/employees/department/{department}` | By department |
| GET    | `/api/employees/job-title/{jobTitle}` | By job title |

---

## 🧬 Employee Model
- id (Long)  
- firstName (String)  
- lastName (String)  
- email (String, unique)  
- phoneNumber (String)  
- dateOfBirth (LocalDate)  
- hireDate (LocalDate)  
- jobTitle (String)  
- salary (Double)  
- department (String)  
- active (boolean)

---

## 📸 Screenshots

Add images in: `screenshots/`

```
screenshots/dashboard.png
screenshots/add-employee.png
screenshots/edit-employee.png
screenshots/swagger.png
```

Then they will appear:

![Dashboard](screenshots/dashboard.png)
![Add Employee](screenshots/add-employee.png)
![Swagger](screenshots/swagger.png)

---

## 🚀 Future Improvements
- Pagination, search & filtering  
- Spring Security (role-based)  
- Unit + integration tests  
- GitHub Actions CI pipeline  
- Cloud deployment (Railway/AWS/Azure)

---

## 🤝 Contributing
See `CONTRIBUTING.md`.

---

## 📜 License
Licensed under the MIT License — see `LICENSE`.

---

## ✉️ Contact
Dinesh Chandra — TheComputationalCore  
GitHub: https://github.com/TheComputationalCore  
YouTube: https://www.youtube.com/@TheComputationalCore

