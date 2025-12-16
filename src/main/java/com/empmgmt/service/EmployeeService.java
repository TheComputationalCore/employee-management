package com.empmgmt.service;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;
import com.empmgmt.model.Employee;

import java.util.List;

public interface EmployeeService {

    /* ===========================
       CRUD
       =========================== */
    EmployeeDTO createEmployee(EmployeeDTO dto);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO dto);

    void softDeleteEmployee(Long id);

    void restoreEmployee(Long id);

    EmployeeDTO getEmployee(Long id);

    /* ===========================
       SEARCH & LISTING
       =========================== */
    PaginatedResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest req);

    List<EmployeeDTO> getAllEmployees();

    /* ===========================
       🔥 SECURITY / AUTH SUPPORT
       =========================== */

    /**
     * Used by controllers that receive email/username from Spring Security
     */
    Employee getByEmail(String email);

    /**
     * Universal method for all `/my` pages
     * Attendance, Leaves, Payroll, Performance, Onboarding
     */
    Long getEmployeeIdFromAuth();
}
