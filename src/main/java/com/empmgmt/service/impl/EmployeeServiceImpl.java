package com.empmgmt.service.impl;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.mapper.EmployeeMapper;
import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.service.CustomUserDetails;
import com.empmgmt.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    /* =========================================================
       SAFE SORT PARSER (Prevents crashes & SQL injection)
    ========================================================= */
    private Sort getSafeSort(String sortValue) {

        if (sortValue == null || !sortValue.contains(",")) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String[] arr = sortValue.split(",");

        String field = arr[0].trim();
        String direction = arr.length > 1 ? arr[1].trim() : "asc";

        return Sort.by(
                Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC),
                field
        );
    }

    /* =========================================================
       CREATE EMPLOYEE
    ========================================================= */
    @Override
    public EmployeeDTO createEmployee(EmployeeDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = mapper.toEntity(dto);
        employee.setStatus(EmployeeStatus.ACTIVE);

        return mapper.toDTO(repo.save(employee));
    }

    /* =========================================================
       UPDATE EMPLOYEE
    ========================================================= */
    @Override
    public EmployeeDTO updateEmployee(Long id, EmployeeDTO dto) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (!employee.getEmail().equals(dto.getEmail())
                && repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(dto.getDepartment());
        employee.setPosition(dto.getPosition());
        employee.setSalary(dto.getSalary());

        return mapper.toDTO(repo.save(employee));
    }

    /* =========================================================
       SOFT DELETE
    ========================================================= */
    @Override
    public void softDeleteEmployee(Long id) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setStatus(EmployeeStatus.INACTIVE);
        repo.save(employee);
    }

    /* =========================================================
       RESTORE
    ========================================================= */
    @Override
    public void restoreEmployee(Long id) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setStatus(EmployeeStatus.ACTIVE);
        repo.save(employee);
    }

    /* =========================================================
       GET SINGLE EMPLOYEE
    ========================================================= */
    @Override
    public EmployeeDTO getEmployee(Long id) {

        return repo.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    /* =========================================================
       SEARCH + PAGINATION (POSTGRES / NEON SAFE)
    ========================================================= */
    @Override
public PaginatedResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest req) {

    Sort sort = getSafeSort(req.getSort());
    Pageable pageable = PageRequest.of(req.getPage(), req.getSize(), sort);

    String search = req.getSearch();
    String department = req.getDepartment();

    Page<Employee> page;

    if (search != null && !search.isBlank()) {
        page = repo.findByStatusAndFirstNameContainingIgnoreCaseOrStatusAndLastNameContainingIgnoreCaseOrStatusAndEmailContainingIgnoreCase(
                EmployeeStatus.ACTIVE, search,
                EmployeeStatus.ACTIVE, search,
                EmployeeStatus.ACTIVE, search,
                pageable
        );
    } else if (department != null && !department.isBlank()) {
        page = repo.findByStatusAndDepartmentContainingIgnoreCaseAndFirstNameContainingIgnoreCase(
                EmployeeStatus.ACTIVE,
                department,
                "",
                pageable
        );
    } else {
        page = repo.findByStatus(EmployeeStatus.ACTIVE, pageable);
    }

    return PaginatedResponse.<EmployeeDTO>builder()
            .content(page.getContent().stream().map(mapper::toDTO).toList())
            .page(page.getNumber())
            .size(page.getSize())
            .totalElements(page.getTotalElements())
            .totalPages(page.getTotalPages())
            .build();
}

    /* =========================================================
       GET ALL (ADMIN / INTERNAL USE)
    ========================================================= */
    @Override
    public List<EmployeeDTO> getAllEmployees() {

        return repo.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }

    /* =========================================================
       AUTH → EMPLOYEE ID
    ========================================================= */
    @Override
    public Long getEmployeeIdFromAuth() {

        Object principal = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getEmployeeId();
        }

        throw new IllegalStateException("User not authenticated");
    }
}
