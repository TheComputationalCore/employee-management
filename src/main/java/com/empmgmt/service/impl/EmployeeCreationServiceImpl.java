package com.empmgmt.service.impl;

import com.empmgmt.model.*;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.model.User;
import com.empmgmt.security.repository.UserRepository;
import com.empmgmt.service.EmployeeCreationService;
import com.empmgmt.service.JobService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;



@Service
@RequiredArgsConstructor
public class EmployeeCreationServiceImpl implements EmployeeCreationService {

    private final EmployeeRepository employeeRepo;
    private final UserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JobService jobService;

        @Override
        @Transactional
        public Long createFromApplication(Application app) {

            if (app == null || app.getJobId() == null) {
                throw new IllegalArgumentException("Invalid application");
            }

            Job job = jobService.getById(app.getJobId());
            if (job == null) {
                throw new IllegalArgumentException("Job not found");
            }

            // ---------------------------
            // Split full name
            // ---------------------------
            String fullName = app.getFullName() != null ? app.getFullName().trim() : "Employee";
            String[] parts = fullName.split("\\s+", 2);
            String firstName = parts[0];
            String lastName = parts.length > 1 ? parts[1] : "";

            // ---------------------------
            // Department from job
            // ---------------------------
            String departmentName = job.getDepartment() != null ? job.getDepartment() : "General";

            // ---------------------------
            // Salary from job (take max or min depending your rule)
            // ---------------------------
            Double offeredSalary = job.getSalaryMax() != null
                    ? job.getSalaryMax()
                    : (job.getSalaryMin() != null ? job.getSalaryMin() : 0.0);

            // ---------------------------
            // Create Employee
            // ---------------------------
            Employee emp = Employee.builder()
                    .firstName(firstName)
                    .lastName(lastName)
                    .email(app.getEmail())
                    .phone(app.getPhone())
                    .position(job.getTitle())     // job title = position
                    .department(departmentName)
                    .salary(offeredSalary)
                    .status(EmployeeStatus.ACTIVE)
                    .createdAt(LocalDateTime.now())
                    .createdBy("HR")
                    .build();

            Employee saved = employeeRepo.save(emp);

            // ---------------------------
            // Create User Account
            // ---------------------------
            User user = User.builder()
                    .username(saved.getId().toString())          // ✔ EMPLOYEE ID for login
                    .password(encoder.encode("employee123"))     // ✔ Default password
                    .role(Role.ROLE_EMPLOYEE)
                    .build();

            userRepo.save(user);

            return saved.getId();
        }
    }


