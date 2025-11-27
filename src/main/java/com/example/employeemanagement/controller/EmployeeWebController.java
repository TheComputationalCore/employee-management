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

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* ============================================================
       DASHBOARD + PAGINATION + SEARCH
       ============================================================ */
    @GetMapping("/")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model
    ) {

        Page<Employee> employeePage = employeeService.getPaginatedEmployees(page, size, keyword);

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalItems", employeePage.getTotalElements());
        model.addAttribute("keyword", keyword);

        // Dashboard counters
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }

    /* ============================================================
       CREATE FORM
       ============================================================ */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Add Employee");
            return "create-employee";
        }

        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("message", "Employee created successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            model.addAttribute("pageTitle", "Add Employee");
            return "create-employee";
        }
    }

    /* ============================================================
       EDIT FORM
       ============================================================ */
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Employee employee = employeeService.getEmployeeById(id);

        model.addAttribute("employee", employee);
        model.addAttribute("pageTitle", "Edit Employee");

        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") Employee employee,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", "Edit Employee");
            return "edit-employee";
        }

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            model.addAttribute("pageTitle", "Edit Employee");
            return "edit-employee";
        }
    }

    /* ============================================================
       DELETE EMPLOYEE
       ============================================================ */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {
        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting employee: " + e.getMessage());
        }

        return "redirect:/web/";
    }
}
