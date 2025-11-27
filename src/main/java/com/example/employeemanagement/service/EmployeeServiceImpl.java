package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    /* ===========================================================
       PAGINATION + SEARCH
    ============================================================ */

    @Override
    public Page<Employee> getPaginatedEmployees(int page, int size, String search) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());

        if (search == null || search.trim().isEmpty()) {
            return repository.findAll(pageable);
        }

        search = "%" + search.toLowerCase() + "%";

        return repository.searchEmployees(search, pageable);
    }

    /* ===========================================================
       BUSINESS CRUD OPERATIONS
    ============================================================ */

    @Override
    @Transactional
    public Employee createEmployee(EmployeeRequestDTO req) {

        if (repository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        }

        Employee employee = new Employee();
        employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName());
        employee.setEmail(req.getEmail());
        employee.setPhoneNumber(req.getPhoneNumber());
        employee.setDepartment(req.getDepartment());
        employee.setPosition(req.getPosition());
        employee.setSalary(req.getSalary());

        return repository.save(employee);
    }


    @Override
    @Transactional
    public Employee updateEmployee(Long id, EmployeeRequestDTO req) {

        Employee employee = getEmployeeById(id);

        // Prevent duplicate email across employees
        if (!employee.getEmail().equals(req.getEmail()) && repository.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + req.getEmail());
        }

        employee.setFirstName(req.getFirstName());
        employee.setLastName(req.getLastName());
        employee.setEmail(req.getEmail());
        employee.setPhoneNumber(req.getPhoneNumber());
        employee.setDepartment(req.getDepartment());
        employee.setPosition(req.getPosition());
        employee.setSalary(req.getSalary());

        return repository.save(employee);
    }


    @Override
    public Employee getEmployeeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee not found with ID: " + id));
    }


    @Override
    @Transactional
    public void deleteEmployee(Long id) {

        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with ID: " + id);
        }

        repository.deleteById(id);
    }


    /* ===========================================================
       DASHBOARD COUNTERS
    ============================================================ */

    @Override
    public long countEmployees() {
        return repository.count();
    }

    @Override
    public long countDepartments() {
        return repository.countDistinctDepartments();
    }

    @Override
    public long countPositions() {
        return repository.countDistinctPositions();
    }
}
