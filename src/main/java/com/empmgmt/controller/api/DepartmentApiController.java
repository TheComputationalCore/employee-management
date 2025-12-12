package com.empmgmt.controller.api;

import com.empmgmt.dto.ApiResponse;
import com.empmgmt.model.Department;
import com.empmgmt.service.DepartmentService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/departments")
@RequiredArgsConstructor
public class DepartmentApiController {

    private final DepartmentService service;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Department>>> getAll() {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Departments fetched", service.getAll())
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Department>> create(@RequestBody Department d) {
        return ResponseEntity.ok(
                new ApiResponse<>(true, "Department created", service.create(d))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Department>> update(
            @PathVariable Long id,
            @RequestBody Department d) {

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Department updated", service.update(id, d))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Department deleted", null));
    }
}
