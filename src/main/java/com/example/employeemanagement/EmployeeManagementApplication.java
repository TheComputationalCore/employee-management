package com.thecomputationalcore.employeemanagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@Slf4j
public class EmployeeManagementApplication {

    public static void main(String[] args) {
        log.info("🚀 Starting Employee Management System...");
        SpringApplication.run(EmployeeManagementApplication.class, args);
        log.info("✅ Employee Management System started successfully!");
    }
}
