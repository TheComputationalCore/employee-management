package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EmployeeService {

    Page<Employee> getEmployees(Pageable pageable);

    Page<Employee> searchEmployees(String keyword, Pageable pageable);

    Employee getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee updated);

    void deleteEmployee(Long id);
}
