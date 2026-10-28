package com.empmgmt.service.impl;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;
import com.empmgmt.exception.ResourceNotFoundException;
import com.empmgmt.mapper.EmployeeMapper;
import com.empmgmt.model.Employee;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.model.Role;
import com.empmgmt.repository.EmployeeRepository;
import com.empmgmt.security.model.User;
import com.empmgmt.security.repository.UserRepository;
import com.empmgmt.security.service.CustomUserDetails;
import com.empmgmt.security.service.PasswordSetupService;
import com.empmgmt.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private static final Set<String> SORTABLE_FIELDS = Set.of(
            "id", "firstName", "lastName", "email", "department", "position", "salary"
    );
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /* =========================================================
       SAFE SORT PARSER
       ========================================================= */
    private Sort getSafeSort(String sortValue) {
        if (sortValue == null || sortValue.isBlank()) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        String[] arr = sortValue.split(",");
        String field = arr[0].trim();
        String direction = arr.length > 1 ? arr[1].trim() : "asc";

        if (arr.length > 2 || !SORTABLE_FIELDS.contains(field)) {
            return Sort.by(Sort.Direction.ASC, "id");
        }

        return Sort.by(
                Sort.Direction.fromOptionalString(direction).orElse(Sort.Direction.ASC),
                field
        );
    }

    /* =========================================================
       CREATE
       ========================================================= */
    @Override
    @Transactional
    public EmployeeDTO createEmployee(EmployeeDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }

        Employee employee = mapper.toEntity(dto);
        employee.setStatus(EmployeeStatus.ACTIVE);
        Employee savedEmployee = repo.save(employee);

        userRepository.save(User.builder()
                .username(savedEmployee.getId().toString())
                .password(passwordEncoder.encode(PasswordSetupService.randomSecret()))
                .role(Role.ROLE_EMPLOYEE)
                .employee(savedEmployee)
                .build());

        return mapper.toDTO(savedEmployee);
    }

    /* =========================================================
       UPDATE
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
       SOFT DELETE / RESTORE
       ========================================================= */
    @Override
    public void softDeleteEmployee(Long id) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setStatus(EmployeeStatus.INACTIVE);
        repo.save(employee);
    }

    @Override
    public void restoreEmployee(Long id) {

        Employee employee = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        employee.setStatus(EmployeeStatus.ACTIVE);
        repo.save(employee);
    }

    /* =========================================================
       GET ONE
       ========================================================= */
    @Override
    public EmployeeDTO getEmployee(Long id) {

        return repo.findById(id)
                .map(mapper::toDTO)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    /* =========================================================
       SEARCH (FINAL, SAFE, CORRECT)
       ========================================================= */
    @Override
    public PaginatedResponse<EmployeeDTO> searchEmployees(EmployeeSearchRequest req) {

        int pageNumber = req.getPage() == null ? 0 : Math.max(0, req.getPage());
        int pageSize = req.getSize() == null || req.getSize() < 1
                ? DEFAULT_PAGE_SIZE
                : Math.min(req.getSize(), MAX_PAGE_SIZE);
        Pageable pageable = PageRequest.of(pageNumber, pageSize, getSafeSort(req.getSort()));

        Page<Employee> page;
        EmployeeStatus status = req.getStatus() == null ? EmployeeStatus.ACTIVE : req.getStatus();

        if (req.getSearch() != null && !req.getSearch().isBlank()) {

            page = repo.searchByStatus(
                    status,
                    req.getSearch(),
                    pageable
            );

        } else if (req.getDepartment() != null && !req.getDepartment().isBlank()) {

            page = repo.findByStatusAndDepartmentContainingIgnoreCase(
                    status,
                    req.getDepartment(),
                    pageable
            );

        } else {

            page = repo.findByStatus(
                    status,
                    pageable
            );
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
       GET ALL
       ========================================================= */
    @Override
    public List<EmployeeDTO> getAllEmployees() {

        return repo.findAll()
                .stream()
                .map(mapper::toDTO)
                .toList();
    }

    /* =========================================================
       EMAIL → EMPLOYEE
       ========================================================= */
    @Override
    public Employee getByEmail(String email) {

        return repo.findByEmail(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Employee not found for email: " + email
                        )
                );
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
