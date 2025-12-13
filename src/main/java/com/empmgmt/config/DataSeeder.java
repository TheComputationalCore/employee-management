package com.empmgmt.config;

import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import com.empmgmt.security.model.User;
import com.empmgmt.security.repository.UserRepository;
import com.empmgmt.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepo;
    private final EmployeeRepository employeeRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {

        if (userRepo.count() > 0) {
            return; // Prevent reseeding
        }

        LocalDateTime now = LocalDateTime.now();

        // ==========================================================================
        // CREATE EMPLOYEES
        // ==========================================================================

        Employee adminEmp = employeeRepo.save(
                Employee.builder()
                        .firstName("Admin")
                        .lastName("User")
                        .email("admin@ems.com")
                        .department("HR")
                        .position("Administrator")
                        .salary(0.0)
                        .status(EmployeeStatus.ACTIVE)          // ✅ REQUIRED
                        .createdAt(now)                          // ✅ REQUIRED
                        .createdBy("SYSTEM")                     // ✅ REQUIRED
                        .build()
        );

        Employee hrEmp = employeeRepo.save(
                Employee.builder()
                        .firstName("HR")
                        .lastName("Manager")
                        .email("hr@ems.com")
                        .department("HR")
                        .position("HR Manager")
                        .salary(0.0)
                        .status(EmployeeStatus.ACTIVE)
                        .createdAt(now)
                        .createdBy("SYSTEM")
                        .build()
        );

        Employee employeeEmp = employeeRepo.save(
                Employee.builder()
                        .firstName("John")
                        .lastName("Employee")
                        .email("employee@ems.com")
                        .department("IT")
                        .position("Developer")
                        .salary(30000.0)
                        .status(EmployeeStatus.ACTIVE)
                        .createdAt(now)
                        .createdBy("SYSTEM")
                        .build()
        );

        // ==========================================================================
        // CREATE USERS LINKED TO EMPLOYEES
        // ==========================================================================

        User admin = User.builder()
                .username("admin")
                .password(encoder.encode("admin123"))
                .role(Role.ROLE_ADMIN)
                .employee(adminEmp)
                .build();

        User hr = User.builder()
                .username("hr")
                .password(encoder.encode("hr123"))
                .role(Role.ROLE_HR)
                .employee(hrEmp)
                .build();

        User employee = User.builder()
                .username("employee")
                .password(encoder.encode("emp123"))
                .role(Role.ROLE_EMPLOYEE)
                .employee(employeeEmp)
                .build();

        userRepo.save(admin);
        userRepo.save(hr);
        userRepo.save(employee);

        System.out.println("=========================================================");
        System.out.println(" USERS + EMPLOYEES SEEDED ");
        System.out.println("=========================================================");
        System.out.println("ADMIN: admin / admin123");
        System.out.println("HR: hr / hr123");
        System.out.println("EMPLOYEE: employee / emp123");
        System.out.println("=========================================================");
    }
}
