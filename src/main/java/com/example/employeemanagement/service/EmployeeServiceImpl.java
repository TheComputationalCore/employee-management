package com.example.employeemanagement.service;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeWebDTO;
import com.example.employeemanagement.mapper.EmployeeMapper;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.repository.EmployeeRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository repo;
    private final EmployeeMapper mapper;

    /* ---------------------------
        CRUD Operations
    --------------------------- */

    @Override
    public EmployeeWebDTO createEmployee(EmployeeRequestDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        Employee employee = mapper.toEntity(dto);
        repo.save(employee);

        return mapper.toWebDTO(employee);
    }

    @Override
    public EmployeeWebDTO updateEmployee(Long id, EmployeeRequestDTO dto) {
        Employee existing = repo.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));

        // Email uniqueness
        if (!existing.getEmail().equals(dto.getEmail()) &&
                repo.existsByEmail(dto.getEmail())) {
            throw new IllegalArgumentException("Email already exists: " + dto.getEmail());
        }

        mapper.updateEntity(existing, dto);
        repo.save(existing);

        return mapper.toWebDTO(existing);
    }

    @Override
    public void deleteEmployee(Long id) {
        if (!repo.existsById(id)) {
            throw new EntityNotFoundException("Employee not found");
        }
        repo.deleteById(id);
    }

    @Override
    public EmployeeWebDTO getEmployeeById(Long id) {
        return repo.findById(id)
                .map(mapper::toWebDTO)
                .orElseThrow(() -> new EntityNotFoundException("Employee not found"));
    }


    /* ---------------------------
        Pagination + Search
    --------------------------- */

    @Override
    public List<EmployeeWebDTO> getPaginatedEmployees(
            int page,
            int size,
            String sortField,
            String sortDir,
            String keyword
    ) {

        List<Employee> source = (keyword == null || keyword.isBlank())
                ? repo.findAll()
                : repo.search(keyword);

        // Sorting
        Comparator<Employee> comparator = getComparator(sortField);

        if ("desc".equalsIgnoreCase(sortDir)) {
            comparator = comparator.reversed();
        }

        List<Employee> sorted = source.stream()
                .sorted(comparator)
                .collect(Collectors.toList());

        // Pagination window
        int start = page * size;
        int end = Math.min(start + size, sorted.size());

        if (start > sorted.size()) {
            return List.of();
        }

        return sorted.subList(start, end)
                .stream()
                .map(mapper::toWebDTO)
                .collect(Collectors.toList());
    }

    @Override
    public long getTotalEmployeeCount(String keyword) {
        return (keyword == null || keyword.isBlank())
                ? repo.count()
                : repo.search(keyword).size();
    }


    /* ---------------------------
        Dashboard Counters
    --------------------------- */

    @Override
    public long countEmployees() {
        return repo.countEmployees();
    }

    @Override
    public long countDepartments() {
        return repo.countDepartments();
    }

    @Override
    public long countPositions() {
        return repo.countPositions();
    }


    /* ---------------------------
        Helper
    --------------------------- */

    private Comparator<Employee> getComparator(String sortField) {
        return switch (sortField) {
            case "email"      -> Comparator.comparing(Employee::getEmail, String.CASE_INSENSITIVE_ORDER);
            case "department" -> Comparator.comparing(Employee::getDepartment, String.CASE_INSENSITIVE_ORDER);
            case "position"   -> Comparator.comparing(Employee::getPosition, String.CASE_INSENSITIVE_ORDER);
            case "salary"     -> Comparator.comparing(Employee::getSalary);
            default           -> Comparator.comparing(Employee::getFirstName, String.CASE_INSENSITIVE_ORDER);
        };
    }
}
