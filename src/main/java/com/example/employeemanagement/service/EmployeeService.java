package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    // Pagination + Search
    Page<Employee> getPaginatedEmployees(int page, int size, String search);

    // Dashboard KPIs
    long countEmployees();
    long countDepartments();
    long countPositions();

    // CRUD
    Employee getEmployeeById(Long id);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employeeDetails);
    void deleteEmployee(Long id);
}
