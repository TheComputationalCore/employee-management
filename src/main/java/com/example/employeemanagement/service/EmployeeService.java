package com.thecomputationalcore.employeemanagement.service;

import com.thecomputationalcore.employeemanagement.model.Employee;
import org.springframework.data.domain.Page;

public interface EmployeeService {

    /**
     * Paginated + sorted + searchable employee list.
     *
     * @param page   page number (0-based)
     * @param size   number of records per page
     * @param sort   clean sort key: name | department | position | salary
     * @param dir    asc or desc
     * @param search search keyword (nullable or empty)
     */
    Page<Employee> getEmployees(int page, int size, String sort, String dir, String search);


    Employee getEmployeeById(Long id);

    Employee createEmployee(Employee employee);

    Employee updateEmployee(Long id, Employee employee);

    void deleteEmployee(Long id);
}
