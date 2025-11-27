package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeDTO;
import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.service.EmployeeService;

import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService service;

    /* -------------------------------------------------------------
        DASHBOARD + PAGINATION + SORTING + SEARCH IN ONE ENDPOINT
       ------------------------------------------------------------- */
    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "firstName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search,
            Model model
    ) {

        Page<EmployeeDTO> employees;

        if (search != null && !search.isBlank()) {
            employees = service.searchEmployees(search, page, size);
        } else {
            employees = service.getEmployees(page, size, sortField, sortDir);
        }

        /* ---- Dashboard Stats ---- */
        long totalEmployees = employees.getTotalElements();
        long departments = employees.stream().map(EmployeeDTO::getDepartment).distinct().count();
        long positions = employees.stream().map(EmployeeDTO::getPosition).distinct().count();

        model.addAttribute("employees", employees.getContent());
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("departmentsCount", departments);
        model.addAttribute("positionsCount", positions);

        /* ---- Pagination Data ---- */
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employees.getTotalPages());
        model.addAttribute("totalItems", employees.getTotalElements());

        /* ---- Sorting / Search ---- */
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }


    /* -------------------------------------------------------------
        CREATE EMPLOYEE
       ------------------------------------------------------------- */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDTO());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(
            @Valid @ModelAttribute("employee") EmployeeRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirect
    ) {
        if (bindingResult.hasErrors()) {
            return "create-employee";
        }

        try {
            service.createEmployee(request);
            redirect.addFlashAttribute("message", "Employee created successfully!");
        } catch (Exception ex) {
            bindingResult.rejectValue("email", "error.employee", ex.getMessage());
            return "create-employee";
        }

        return "redirect:/web/";
    }


    /* -------------------------------------------------------------
        EDIT EMPLOYEE
       ------------------------------------------------------------- */
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        EmployeeDTO dto = service.getEmployeeById(id);

        EmployeeRequestDTO request = new EmployeeRequestDTO();
        request.setFirstName(dto.getFirstName());
        request.setLastName(dto.getLastName());
        request.setEmail(dto.getEmail());
        request.setPhoneNumber(dto.getPhoneNumber());
        request.setDepartment(dto.getDepartment());
        request.setPosition(dto.getPosition());
        request.setSalary(dto.getSalary());

        model.addAttribute("employee", request);
        model.addAttribute("employeeId", id);
        model.addAttribute("pageTitle", "Edit Employee");

        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") EmployeeRequestDTO request,
            BindingResult bindingResult,
            RedirectAttributes redirect
    ) {
        if (bindingResult.hasErrors()) {
            return "edit-employee";
        }

        try {
            service.updateEmployee(id, request);
            redirect.addFlashAttribute("message", "Employee updated successfully!");
        } catch (IllegalArgumentException ex) {
            bindingResult.rejectValue("email", "error.employee", ex.getMessage());
            return "edit-employee";
        }

        return "redirect:/web/";
    }

    /* -------------------------------------------------------------
        DELETE EMPLOYEE
       ------------------------------------------------------------- */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirect) {
        service.deleteEmployee(id);
        redirect.addFlashAttribute("message", "Employee deleted successfully!");
        return "redirect:/web/";
    }
}
