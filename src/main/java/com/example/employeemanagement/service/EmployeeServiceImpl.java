package com.example.employeemanagement.service;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    /* ============================================================
       PAGINATION SERVICE
       ============================================================ */
    @Override
    public Page<Employee> getAllEmployeesPaginated(Pageable pageable) {
        return employeeRepository.findAll(pageable);
    }

    /* ============================================================
       BASIC LIST (NO PAGINATION)
       ============================================================ */
    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    /* ============================================================
       GET SINGLE EMPLOYEE
       ============================================================ */
    @Override
    public Employee getEmployeeById(Long id) {
        return employeeRepository.findById(id)
                .orElseThrow(() ->
                        new EntityNotFoundException("Employee not found with ID: " + id));
    }

    /* ============================================================
       CREATE EMPLOYEE
       ============================================================ */
    @Override
    public Employee createEmployee(Employee employee) {

        if (employeeRepository.existsByEmail(employee.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + employee.getEmail());
        }

        return employeeRepository.save(employee);
    }

    /* ============================================================
       UPDATE EMPLOYEE
       ============================================================ */
    @Override
    public Employee updateEmployee(Long id, Employee employeeDetails) {

        Employee employee = getEmployeeById(id);

        // Prevent duplicate email
        if (!employee.getEmail().equals(employeeDetails.getEmail()) &&
            employeeRepository.existsByEmail(employeeDetails.getEmail())) {

            throw new IllegalArgumentException(
                    "Email already exists: " + employeeDetails.getEmail());
        }

        employee.setFirstName(employeeDetails.getFirstName());
        employee.setLastName(employeeDetails.getLastName());
        employee.setEmail(employeeDetails.getEmail());
        employee.setPhoneNumber(employeeDetails.getPhoneNumber());
        employee.setDepartment(employeeDetails.getDepartment());
        employee.setPosition(employeeDetails.getPosition());
        employee.setSalary(employeeDetails.getSalary());

        return employeeRepository.save(employee);
    }

    /* ============================================================
       DELETE EMPLOYEE
       ============================================================ */
    @Override
    public void deleteEmployee(Long id) {

        if (!employeeRepository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found with ID: " + id);
        }

        employeeRepository.deleteById(id);
    }

    /* ============================================================
       FILTERS
       ============================================================ */
    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    @Override
    public List<Employee> getEmployeesByPosition(String position) {
        return employeeRepository.findByPosition(position);
    }
}
