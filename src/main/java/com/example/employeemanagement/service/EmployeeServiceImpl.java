package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;

    @Override
    public Page<Employee> getEmployees(Pageable pageable) {
        return repo.findAll(pageable);
    }

    @Override
    public Page<Employee> searchEmployees(String keyword, Pageable pageable) {
        return repo.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrDepartmentContainingIgnoreCase(
                keyword, keyword, keyword, keyword, pageable
        );
    }

    @Override
    public Employee getEmployeeById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }

    @Override
    public Employee createEmployee(Employee employee) {
        if (repo.existsByEmail(employee.getEmail()))
            throw new IllegalArgumentException("Email already exists.");

        return repo.save(employee);
    }

    @Override
    public Employee updateEmployee(Long id, Employee updated) {
        Employee emp = getEmployeeById(id);

        if (!emp.getEmail().equals(updated.getEmail()) && repo.existsByEmail(updated.getEmail()))
            throw new IllegalArgumentException("Email already exists.");

        emp.setFirstName(updated.getFirstName());
        emp.setLastName(updated.getLastName());
        emp.setEmail(updated.getEmail());
        emp.setPhoneNumber(updated.getPhoneNumber());
        emp.setDepartment(updated.getDepartment());
        emp.setPosition(updated.getPosition());
        emp.setSalary(updated.getSalary());

        return repo.save(emp);
    }

    @Override
    public void deleteEmployee(Long id) {
        repo.deleteById(id);
    }
}
