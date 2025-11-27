package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.mapper.EmployeeMapper;
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

    private final EmployeeRepository repository;
    private final EmployeeMapper mapper;

    /* ================================
          PAGINATION + SORTING
       ================================ */
    @Override
    public Page<EmployeeDTO> getEmployees(int page, int size, String sortField, String sortDir) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortField).descending()
                : Sort.by(sortField).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        return repository.findAll(pageable)
                .map(mapper::toDTO);
    }

    /* ================================
          SEARCH
       ================================ */
    @Override
    public Page<EmployeeDTO> searchEmployees(String keyword, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return repository
                .findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
                        keyword, keyword, pageable
                )
                .map(mapper::toDTO);
    }

    /* ================================
          GET ONE
       ================================ */
    @Override
    public EmployeeDTO getEmployeeById(Long id) {
        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        return mapper.toDTO(employee);
    }

    /* ================================
          CREATE
       ================================ */
    @Override
    public EmployeeDTO createEmployee(EmployeeRequestDTO request) {

        if (repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = mapper.toEntity(request);
        return mapper.toDTO(repository.save(employee));
    }

    /* ================================
          UPDATE
       ================================ */
    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeRequestDTO request) {

        Employee employee = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        if (!employee.getEmail().equals(request.getEmail())
                && repository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        mapper.updateEntity(employee, request);
        return mapper.toDTO(repository.save(employee));
    }

    /* ================================
          DELETE
       ================================ */
    @Override
    public void deleteEmployee(Long id) {
        if (!repository.existsById(id)) {
            throw new EntityNotFoundException("Employee not found");
        }
        repository.deleteById(id);
    }
}
