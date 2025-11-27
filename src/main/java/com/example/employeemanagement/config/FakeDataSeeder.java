package com.example.employeemanagement.config;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FakeDataSeeder {

    private final EmployeeRepository repo;

    @PostConstruct
    public void seed() {
        if (repo.count() > 0) return;

        repo.save(Employee.builder().firstName("John").lastName("Doe")
                .email("john@example.com").department("HR")
                .position("Manager").salary(75000.0).build());

        repo.save(Employee.builder().firstName("Sarah").lastName("Wilson")
                .email("sarah@example.com").department("IT")
                .position("Developer").salary(82000.0).build());
    }
}
