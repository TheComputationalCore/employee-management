package com.empmgmt.service;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;

public interface EmployeeService {

    EmployeeDTO createEmployee(EmployeeDTO dto);

    EmployeeDTO updateEmployee(Long id, EmployeeDTO dto);

    void softDeleteEmployee(Long id);

    void restoreEmployee(Long id);

    EmployeeDTO getEmployee(Long id);

    PaginatedResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest req);

    Object getAllEmployees();

    Long getEmployeeIdFromAuth();
}
