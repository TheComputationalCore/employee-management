package com.empmgmt.controller.api;

import com.empmgmt.dto.ApiResponse;
import com.empmgmt.service.DashboardService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardApiController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getDashboardStats() {

        Map<String, Object> stats = Map.of(
                "totalEmployees", dashboardService.getTotalEmployees(),
                "totalDepartments", dashboardService.getTotalDepartments(),
                "employeesPerDepartment", dashboardService.getEmployeesPerDepartment(),
                "recentEmployees", dashboardService.getRecentEmployees(5),
                "averageSalary", dashboardService.getAverageSalary()
        );

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Dashboard data", stats)
        );
    }
}
