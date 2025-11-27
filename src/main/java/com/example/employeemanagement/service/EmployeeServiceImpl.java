package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeFilterRequest;
import com.example.employeemanagement.dto.PagedResponse;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public PagedResponse<Employee> getEmployees(int page, int size, EmployeeFilterRequest filter) {

        Sort sort = filter.getSortDir().equalsIgnoreCase("desc") ?
                Sort.by(filter.getSortField()).descending() :
                Sort.by(filter.getSortField()).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Employee> result;

        boolean searching = filter.getSearch() != null && !filter.getSearch().isBlank();
        boolean dept = filter.getDepartment() != null && !filter.getDepartment().isBlank();
        boolean pos = filter.getPosition() != null && !filter.getPosition().isBlank();

        if (searching) {
            result = employeeRepository
                    .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                            filter.getSearch(), filter.getSearch(), filter.getSearch(), pageable);
        } else if (dept) {
            result = employeeRepository.findByDepartmentIgnoreCase(filter.getDepartment(), pageable);
        } else if (pos) {
            result = employeeRepository.findByPositionIgnoreCase(filter.getPosition(), pageable);
        } else {
            result = employeeRepository.findAll(pageable);
        }

        return new PagedResponse<>(result);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.existsByEmail(employee.getEmail()))
            throw new IllegalArgumentException("Email already exists");

        return employeeRepository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id);

        if (!existing.getEmail().equals(updated.getEmail())
                && employeeRepository.existsByEmail(updated.getEmail()))
            throw new IllegalArgumentException("Email already exists");

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
        getEmployeeById(id);
        employeeRepository.deleteById(id);
    }
}
