package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    /* ============================================================
       PAGINATION + SEARCH
       ============================================================ */
    @Override
    public Page<Employee> getPaginatedEmployees(int page, int size, String search) {

        Pageable pageable = PageRequest.of(
                Math.max(page - 1, 0),
                size,
                Sort.by("id").descending()
        );

        if (search != null && !search.trim().isEmpty()) {
            String keyword = "%" + search.trim().toLowerCase() + "%";

            return employeeRepository.searchEmployees(keyword, pageable);
        }

        return employeeRepository.findAll(pageable);
    }

    /* ============================================================
       DASHBOARD METRICS
       ============================================================ */

    @Override
    public long countEmployees() {
        return employeeRepository.count();
    }

    @Override
    public long countDepartments() {
        return employeeRepository.countDistinctDepartments();
    }

    @Override
    public long countPositions() {
        return employeeRepository.countDistinctPositions();
    }

    /* ============================================================
       CRUD OPERATIONS
       ============================================================ */

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee not found with ID: " + id));
    }

    @Override
    public Employee createEmployee(Employee employee) {

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee details) {

        Employee employee = getEmployeeById(id);

        // Handling email uniqueness check
        if (!employee.getEmail().equals(details.getEmail())) {
            if (employeeRepository.existsByEmail(details.getEmail())) {
                throw new IllegalArgumentException("Email already exists: " + details.getEmail());
            }
        }

        employee.setFirstName(details.getFirstName());
        employee.setLastName(details.getLastName());
        employee.setEmail(details.getEmail());
        employee.setPhoneNumber(details.getPhoneNumber());
        employee.setDepartment(details.getDepartment());
        employee.setPosition(details.getPosition());
        employee.setSalary(details.getSalary());

        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee ID not found: " + id);
        }

        employeeRepository.deleteById(id);
    }
}
