package com.thecomputationalcore.employeemanagement.service;

import com.thecomputationalcore.employeemanagement.model.Employee;
import com.thecomputationalcore.employeemanagement.repository.EmployeeRepository;

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

    /**
     * Maps clean sort keys to actual DB fields.
     */
    private Sort getSortDirection(String sortKey, String dir) {

        Sort.Direction direction =
                dir != null && dir.equalsIgnoreCase("desc")
                ? Sort.Direction.DESC
                : Sort.Direction.ASC;

        return switch (sortKey == null ? "name" : sortKey) {

            case "department" -> Sort.by(direction, "department");
            case "position"   -> Sort.by(direction, "position");
            case "salary"     -> Sort.by(direction, "salary");

            // Default: name sorting → firstName then lastName
            case "name", default -> Sort.by(direction, "firstName")
                                        .and(Sort.by(direction, "lastName"));
        };
    }


    @Override
    public Page<Employee> getEmployees(int page, int size, String sort, String dir, String search) {

        Sort sorting = getSortDirection(sort, dir);
        Pageable pageable = PageRequest.of(page, size, sorting);

        // If search keyword exists → universal search
        if (search != null && !search.trim().isEmpty()) {
            return employeeRepository.searchAll(search.trim(), pageable);
        }

        // No search → normal pagination
        return employeeRepository.findAllEmployees(pageable);
    }


    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee not found: " + id));
    }


    @Override
    public Employee createEmployee(Employee employee) {
        return employeeRepository.save(employee);
    }


    @Override
    public Employee updateEmployee(Long id, Employee updated) {

        Employee existing = getEmployeeById(id);

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setDepartment(updated.getDepartment());
        existing.setPosition(updated.getPosition());
        existing.setSalary(updated.getSalary());

        return employeeRepository.save(existing);
    }


    @Override
    public void deleteEmployee(Long id) {
        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found: " + id);
        }
        employeeRepository.deleteById(id);
    }
}
