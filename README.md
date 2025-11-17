# Employee Management System

A Spring Boot application for managing employee information with a modern web interface using Thymeleaf, Bootstrap, and REST APIs.

## Features

- Create, Read, Update, and Delete (CRUD) operations for employee records
- Modern and responsive user interface using Bootstrap 5
- Form validation (both client-side and server-side)
- RESTful API endpoints with Swagger documentation
- MySQL database integration
- Clean and maintainable code structure

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher

## Setup

1. Clone the repository:
   ```bash
 git clone https://github.com/TheComputationalCore/employee-management.git
   ```

2. Configure MySQL:
   - Create a MySQL database (it will be created automatically if using the provided configuration)
   - Update `src/main/resources/application.properties` with your MySQL credentials if different from the defaults

3. Build the project:
   ```bash
   mvn clean install
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

5. Access the application:
   - Web Interface: http://localhost:8080
   - Swagger Documentation: http://localhost:8080/swagger-ui.html

## API Endpoints

### Web Interface Endpoints

- `GET /` - Home page with employee list
- `GET /employees/new` - Form to add new employee
- `POST /employees/new` - Create new employee
- `GET /employees/edit/{id}` - Form to edit employee
- `POST /employees/edit/{id}` - Update employee
- `GET /employees/delete/{id}` - Delete employee

### REST API Endpoints

- `GET /api/employees` - Get all employees
- `GET /api/employees/{id}` - Get employee by ID
- `POST /api/employees` - Create new employee
- `PUT /api/employees/{id}` - Update employee
- `DELETE /api/employees/{id}` - Delete employee
- `GET /api/employees/department/{department}` - Get employees by department
- `GET /api/employees/job-title/{jobTitle}` - Get employees by job title

## Data Model

The Employee entity includes the following fields:

- `id` (Long) - Primary key
- `firstName` (String) - Employee's first name
- `lastName` (String) - Employee's last name
- `email` (String) - Employee's email address (unique)
- `phoneNumber` (String) - Employee's phone number
- `dateOfBirth` (LocalDate) - Employee's date of birth
- `hireDate` (LocalDate) - Employee's hire date
- `jobTitle` (String) - Employee's job title
- `salary` (Double) - Employee's salary
- `department` (String) - Employee's department
- `active` (boolean) - Employee's active status

## Validation Rules

- First name and last name: 2-50 characters
- Email: Valid email format and unique
- Phone number: Valid format (+1-234-567-8900)
- Date of birth: Must be in the past and at least 18 years ago
- Hire date: Must be in the past or present
- Salary: Must be positive

## Technology Stack

- Spring Boot 3.2.3
- Spring Data JPA
- Thymeleaf
- MySQL
- Bootstrap 5
- jQuery
- Swagger/OpenAPI
- Maven

## Contributing

1. Fork the repository
2. Create your feature branch
3. Commit your changes
4. Push to the branch
5. Create a new Pull Request

