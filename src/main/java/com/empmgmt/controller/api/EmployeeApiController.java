package com.empmgmt.controller.api;

import com.empmgmt.dto.*;
import com.empmgmt.service.EmployeeService;
import lombok.RequiredArgsConstructor;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
@RequiredArgsConstructor
public class EmployeeApiController {

    private final EmployeeService service;

    @PostMapping("/search")
    public ResponseEntity<ApiResponse<PaginatedResponse<EmployeeDTO>>> search(
            @RequestBody EmployeeSearchRequest req) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Employees fetched", service.searchEmployees(req))
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmployeeDTO>> create(@Valid @RequestBody EmployeeDTO dto) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Employee created", service.createEmployee(dto))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> update(
            @PathVariable Long id,
            @Valid @RequestBody EmployeeDTO dto) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Employee updated", service.updateEmployee(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.softDeleteEmployee(id);   // FIXED: correct method name
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Employee deleted", null)
        );
    }
}
