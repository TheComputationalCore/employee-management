package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.EmployeeRequestDTO;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    Page<EmployeeDTO> getEmployees(int page, int size, String sortField, String sortDir);

    Page<EmployeeDTO> searchEmployees(String keyword, int page, int size);

    EmployeeDTO getEmployeeById(Long id);

    EmployeeDTO createEmployee(EmployeeRequestDTO request);

    EmployeeDTO updateEmployee(Long id, EmployeeRequestDTO request);

    void deleteEmployee(Long id);
}
