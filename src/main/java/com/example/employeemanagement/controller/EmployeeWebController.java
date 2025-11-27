package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* ==========================================================
       DASHBOARD + PAGINATED EMPLOYEE TABLE
       ========================================================== */
    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "firstName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "") String keyword,
            Model model
    ) {

        Page<Employee> employeesPage = employeeService.getPaginatedEmployees(
                page, size, sortField, sortDir, keyword
        );

        // Dashboard Stats
        long totalEmployees = employeesPage.getTotalElements();
        long departments = employeesPage
                .stream()
                .map(Employee::getDepartment)
                .filter(Objects::nonNull)
                .filter(d -> !d.isBlank())
                .collect(Collectors.toSet())
                .size();

        long positions = employeesPage
                .stream()
                .map(Employee::getPosition)
                .filter(Objects::nonNull)
                .filter(p -> !p.isBlank())
                .collect(Collectors.toSet())
                .size();

        /* Pagination Data */
        model.addAttribute("employees", employeesPage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeesPage.getTotalPages());
        model.addAttribute("totalItems", employeesPage.getTotalElements());

        /* Sorting Data */
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        /* Search Data */
        model.addAttribute("keyword", keyword);

        /* Dashboard Card Counts */
        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("departmentsCount", departments);
        model.addAttribute("positionsCount", positions);

        /* Page Title */
        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }

    /* ==========================================================
       CREATE EMPLOYEE
       ========================================================== */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {

        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add Employee");

        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "create-employee";
        }

        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("message", "Employee added successfully!");
        } catch (Exception ex) {
            bindingResult.rejectValue("email", "error.employee", ex.getMessage());
            return "create-employee";
        }

        return "redirect:/web/";
    }

    /* ==========================================================
       EDIT EMPLOYEE
       ========================================================== */
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("pageTitle", "Edit Employee");

        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        if (bindingResult.hasErrors()) {
            return "edit-employee";
        }

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
        } catch (Exception ex) {
            bindingResult.rejectValue("email", "error.employee", ex.getMessage());
            return "edit-employee";
        }

        return "redirect:/web/";
    }

    /* ==========================================================
       DELETE EMPLOYEE
       ========================================================== */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/";
    }
}
