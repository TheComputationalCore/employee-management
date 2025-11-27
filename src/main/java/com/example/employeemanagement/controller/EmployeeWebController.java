package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class EmployeeWebController {

    private final EmployeeService service;

    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(defaultValue = "") String search,
            Model model
    ) {

        // 1) Parse sort: "field,direction"
        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        Sort.Direction direction = sortParts.length > 1 && sortParts[1].equalsIgnoreCase("desc")
                ? Sort.Direction.DESC : Sort.Direction.ASC;

        // 2) Multi-field sorting for name
        Sort sorting;
        if (sortField.equals("name")) {
            sorting = Sort.by(direction, "firstName").and(Sort.by(direction, "lastName"));
        } else {
            sorting = Sort.by(direction, sortField);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sorting);

        // 3) Choose: search or list all
        Page<Employee> employeePage = search.isBlank()
                ? service.getEmployees(pageable)
                : service.searchEmployees(search.trim(), pageable);

        // 4) Add model attributes
        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalItems", employeePage.getTotalElements());

        model.addAttribute("sort", sort);
        model.addAttribute("sortField", sortField);
        model.addAttribute("direction", direction.toString().toLowerCase());
        model.addAttribute("reverseDirection", direction == Sort.Direction.ASC ? "desc" : "asc");

        model.addAttribute("search", search);
        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }
}
