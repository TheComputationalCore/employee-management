package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repository;

    @Override
    public Page<Employee> getEmployeesPaginated(
            int page,
            int size,
            String keyword,
            String sortField,
            String sortDirection
    ) {
        Sort sort = Sort.by(sortField);
        sort = sortDirection.equalsIgnoreCase("asc")
                ? sort.ascending()
                : sort.descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll(pageable);
        }

        return repository.searchEmployees(keyword.trim(), pageable);
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Override
    public Employee createEmployee(Employee e) {
        if (repository.existsByEmail(e.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return repository.save(e);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updated) {
        Employee existing = getEmployeeById(id);

        if (!existing.getEmail().equals(updated.getEmail())
                && repository.existsByEmail(updated.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setEmail(updated.getEmail());
        existing.setPhoneNumber(updated.getPhoneNumber());
        existing.setDepartment(updated.getDepartment());
        existing.setPosition(updated.getPosition());
        existing.setSalary(updated.getSalary());

        return repository.save(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        repository.deleteById(id);
    }
}
