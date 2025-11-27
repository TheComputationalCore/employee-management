package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService service;

    @GetMapping("/")
    public String dashboard(
            Model model,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "id") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String position
    ) {
        Page<Employee> employees = service.getPaginatedEmployees(
                page, size, sortField, sortDir, search, department, position
        );

        model.addAttribute("employees", employees.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employees.getTotalPages());
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("search", search);
        model.addAttribute("department", department);
        model.addAttribute("position", position);

        model.addAttribute("totalEmployees", service.getTotalEmployees());
        model.addAttribute("departmentsCount", service.getDepartmentsCount());
        model.addAttribute("positionsCount", service.getPositionsCount());

        return "index";
    }
}
