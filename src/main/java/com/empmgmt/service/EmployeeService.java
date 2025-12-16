package com.empmgmt.service;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;
import com.empmgmt.model.Employee;

import java.util.List;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO dto);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO dto);

    void softDeleteEmployee(Long id);

    void restoreEmployee(Long id);

    EmployeeDTO getEmployee(Long id);

    PaginatedResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest req);

    List<EmployeeDTO> getAllEmployees();

    Employee getByEmail(String email);

    /* ======================================================
       AUTH → EMPLOYEE ID (SECURE)
       ====================================================== */
    Long getEmployeeIdFromAuth();
}
