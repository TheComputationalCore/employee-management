package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    Page<Employee> getPaginatedEmployees(
            int page,
            int size,
            String sortField,
            String sortDir,
            String keyword
    );

    Employee getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employeeDetails);

    void deleteEmployee(Long id);
}
