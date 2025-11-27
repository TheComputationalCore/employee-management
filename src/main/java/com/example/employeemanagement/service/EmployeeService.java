package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeWebDTO;

import java.util.List;

public interface EmployeeService {

    /* ---------------------------
       CRUD (Used by Web + API)
    --------------------------- */
    EmployeeWebDTO createEmployee(EmployeeRequestDTO dto);

    EmployeeWebDTO updateEmployee(Long id, EmployeeRequestDTO dto);

    void deleteEmployee(Long id);

    EmployeeWebDTO getEmployeeById(Long id);


    /* ---------------------------
       WEB: Pagination + Search
    --------------------------- */
    List<EmployeeWebDTO> getPaginatedEmployees(
            int page,
            int size,
            String sortField,
            String sortDir,
            String keyword
    );

    long getTotalEmployeeCount(String keyword);


    /* ---------------------------
       Dashboard Counts
    --------------------------- */
    long countEmployees();

    long countDepartments();

    long countPositions();
}
