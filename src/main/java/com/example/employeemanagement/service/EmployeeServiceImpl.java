package com.thecomputationalcore.employeemanagement.service;

import com.thecomputationalcore.employeemanagement.dto.EmployeeDto;
import com.thecomputationalcore.employeemanagement.exception.ResourceNotFoundException;
import com.thecomputationalcore.employeemanagement.mapper.EmployeeMapper;
import com.thecomputationalcore.employeemanagement.model.Employee;
import com.thecomputationalcore.employeemanagement.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeDto> getAllEmployees() {
        log.info("Fetching all employees");
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDto getEmployeeById(Long id) {
        log.info("Fetching employee with ID: {}", id);
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id
                ));
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto createEmployee(EmployeeDto employeeDto) {
        log.info("Creating new employee with email: {}", employeeDto.getEmail());

        if (employeeRepository.existsByEmail(employeeDto.getEmail())) {
            log.warn("Email already exists: {}", employeeDto.getEmail());
            throw new IllegalArgumentException("Email already exists: " + employeeDto.getEmail());
        }

        Employee employee = employeeMapper.toEntity(employeeDto);
        Employee savedEmployee = employeeRepository.save(employee);

        return employeeMapper.toDto(savedEmployee);
    }

    @Override
    public EmployeeDto updateEmployee(Long id, EmployeeDto employeeDto) {
        log.info("Updating employee with ID: {}", id);

        Employee existing = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Employee not found with id: " + id
                ));

        // email change validation
        if (!existing.getEmail().equals(employeeDto.getEmail()) &&
                employeeRepository.existsByEmail(employeeDto.getEmail())) {
            log.warn("Attempt to use existing email: {}", employeeDto.getEmail());
            throw new IllegalArgumentException("Email already exists: " + employeeDto.getEmail());
        }

        existing.setFirstName(employeeDto.getFirstName());
        existing.setLastName(employeeDto.getLastName());
        existing.setEmail(employeeDto.getEmail());
        existing.setPhoneNumber(employeeDto.getPhoneNumber());
        existing.setDepartment(employeeDto.getDepartment());
        existing.setPosition(employeeDto.getPosition());
        existing.setSalary(employeeDto.getSalary());

        Employee updated = employeeRepository.save(existing);

        return employeeMapper.toDto(updated);
    }

    @Override
    public void deleteEmployee(Long id) {
        log.info("Deleting employee with ID: {}", id);

        if (!employeeRepository.existsById(id)) {
            log.error("Delete failed — employee not found with ID {}", id);
            throw new ResourceNotFoundException("Employee not found with id: " + id);
        }

        employeeRepository.deleteById(id);
        log.info("Employee deleted successfully");
    }

    @Override
    public List<EmployeeDto> getEmployeesByDepartment(String department) {
        log.info("Fetching employees by department: {}", department);

        return employeeRepository.findByDepartment(department)
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public List<EmployeeDto> getEmployeesByPosition(String position) {
        log.info("Fetching employees by position: {}", position);

        return employeeRepository.findByPosition(position)
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }
}
