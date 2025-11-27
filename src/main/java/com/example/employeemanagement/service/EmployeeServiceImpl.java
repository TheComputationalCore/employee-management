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
    public Page<Employee> getPaginatedEmployees(
            int page,
            int size,
            String sortField,
            String sortDirection,
            String search,
            String department,
            String position
    ) {
        Sort sort = sortDirection.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        if (search != null && !search.isEmpty()) {
            return repository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                    search, search, pageable
            );
        }

        if (department != null && !department.isEmpty()) {
            return repository.findByDepartmentIgnoreCase(department, pageable);
        }

        if (position != null && !position.isEmpty()) {
            return repository.findByPositionIgnoreCase(position, pageable);
        }

        return repository.findAll(pageable);
    }

    @Override
    public long getTotalEmployees() {
        return repository.count();
    }

    @Override
    public long getDepartmentsCount() {
        return repository.findAll()
                .stream()
                .map(Employee::getDepartment)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .count();
    }

    @Override
    public long getPositionsCount() {
        return repository.findAll()
                .stream()
                .map(Employee::getPosition)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .count();
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if (repository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        return repository.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {
        Employee employee = getEmployeeById(id);

        if (!employee.getEmail().equals(employeeDetails.getEmail()) &&
                repository.existsByEmail(employeeDetails.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setPosition(employeeDetails.getPosition());
        employee.setSalary(employeeDetails.getSalary());

        return repository.save(employee);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found");
        }
        repository.deleteById(id);
    }
}
