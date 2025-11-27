package com.thecomputationalcore.employeemanagement.service;

import com.thecomputationalcore.employeemanagement.dto.EmployeeDto;

import java.util.List;

public interface EmployeeService {

    List<EmployeeDto> getAllEmployees();

    EmployeeDto getEmployeeById(Long id);

    EmployeeDto createEmployee(EmployeeDto employeeDto);

    EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto);

    void deleteEmployee(Long id);

    List<EmployeeDto> getEmployeesByDepartment(String department);

    List<EmployeeDto> getEmployeesByPosition(String position);
}
