package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {

    // Pagination
    Page<Employee> getAllEmployeesPaginated(Pageable pageable);

    // CRUD
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee createEmployee(Employee employee);
    Employee updateEmployee(Long id, Employee employeeDetails);
    void deleteEmployee(Long id);

    // Filters
    List<Employee> getEmployeesByDepartment(String department);
    List<Employee> getEmployeesByPosition(String position);
}
