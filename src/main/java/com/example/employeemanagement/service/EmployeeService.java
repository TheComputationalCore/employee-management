package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeFilterRequest;
import com.example.employeemanagement.dto.PagedResponse;
import com.example.employeemanagement.model.Employee;

public interface EmployeeService {

    PagedResponse<Employee> getEmployees(int page, int size, EmployeeFilterRequest filter);

    Employee getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee updated);

    void deleteEmployee(Long id);
}
