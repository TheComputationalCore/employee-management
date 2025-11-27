package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    Page<Employee> getEmployeesPaginated(
            int page,
            int size,
            String keyword,
            String sortField,
            String sortDirection
    );

    Employee getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee updatedEmployee);

    void deleteEmployee(Long id);
}
