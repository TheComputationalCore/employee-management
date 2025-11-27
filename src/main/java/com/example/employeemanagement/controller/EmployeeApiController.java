package com.thecomputationalcore.employeemanagement.controller;

import com.thecomputationalcore.employeemanagement.dto.EmployeeDto;
import com.thecomputationalcore.employeemanagement.service.EmployeeService;
import com.thecomputationalcore.employeemanagement.util.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Employee API", description = "REST API endpoints for employee management")
public class EmployeeApiController {

    private final EmployeeService employeeService;

    @GetMapping
    @Operation(summary = "Get all employees", description = "Returns a list of all employees")
    public ResponseEntity<List<EmployeeDto>> getAllEmployees() {
        log.info("API: Fetching all employees");
        return ResponseEntity.ok(employeeService.getAllEmployees());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get employee by ID")
    public ResponseEntity<EmployeeDto> getEmployee(@PathVariable Long id) {
        log.info("API: Fetching employee with ID {}", id);
        return ResponseEntity.ok(employeeService.getEmployeeById(id));
    }

    @PostMapping
    @Operation(summary = "Create new employee")
    public ResponseEntity<ApiResponse> createEmployee(@Valid @RequestBody EmployeeDto employeeDto) {
        log.info("API: Creating employee with email {}", employeeDto.getEmail());
        EmployeeDto created = employeeService.createEmployee(employeeDto);
        return ResponseEntity.ok(new ApiResponse(true, "Employee created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update employee")
    public ResponseEntity<ApiResponse> updateEmployee(@PathVariable Long id,
                                                      @Valid @RequestBody EmployeeDto employeeDto) {
        log.info("API: Updating employee with ID {}", id);
        EmployeeDto updated = employeeService.updateEmployee(id, employeeDto);
        return ResponseEntity.ok(new ApiResponse(true, "Employee updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete employee")
    public ResponseEntity<ApiResponse> deleteEmployee(@PathVariable Long id) {
        log.info("API: Deleting employee with ID {}", id);
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponse(true, "Employee deleted successfully"));
    }

    @GetMapping("/department/{department}")
    @Operation(summary = "Get employees by department")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByDepartment(@PathVariable String department) {
        log.info("API: Fetching employees from department {}", department);
        return ResponseEntity.ok(employeeService.getEmployeesByDepartment(department));
    }

    @GetMapping("/position/{position}")
    @Operation(summary = "Get employees by position")
    public ResponseEntity<List<EmployeeDto>> getEmployeesByPosition(@PathVariable String position) {
        log.info("API: Fetching employees with position {}", position);
        return ResponseEntity.ok(employeeService.getEmployeesByPosition(position));
    }
}
