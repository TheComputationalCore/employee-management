package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    /* ===========================================================
       PAGINATION + SEARCH
    ============================================================ */

    /**
     * Returns a paginated + optionally searchable list of employees.
     * 
     * @param page        zero-based page index 
     * @param size        page size
     * @param search      optional search text (name/email/department/position)
     * @return paginated employees
     */
    Page<Employee> getPaginatedEmployees(int page, int size, String search);

    
    /* ===========================================================
       BUSINESS CRUD OPERATIONS
    ============================================================ */

    Employee createEmployee(EmployeeRequestDTO request);

    Employee updateEmployee(Long id, EmployeeRequestDTO request);

    Employee getEmployeeById(Long id);

    void deleteEmployee(Long id);


    /* ===========================================================
       DASHBOARD COUNTERS
    ============================================================ */

    long countEmployees();

    long countDepartments();

    long countPositions();
}
