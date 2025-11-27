package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* -------------------------------------------------------
       DASHBOARD + EMPLOYEE TABLE (with pagination + search)
       ------------------------------------------------------- */
    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            Model model) {

        var result = employeeService.getPaginatedEmployees(page, size, search);

        model.addAttribute("employees", result.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("search", search);

        // Dashboard KPIs
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }

    /* -------------------------------------------------------
       CREATE EMPLOYEE
       ------------------------------------------------------- */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(@ModelAttribute("employee") Employee employee, Model model) {

        try {
            employeeService.createEmployee(employee);
            model.addAttribute("message", "Employee created successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employee", employee);
            return "create-employee";
        }
    }

    /* -------------------------------------------------------
       EDIT EMPLOYEE
       ------------------------------------------------------- */
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("pageTitle", "Edit Employee");
        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @ModelAttribute("employee") Employee employee,
            Model model) {

        try {
            employeeService.updateEmployee(id, employee);
            model.addAttribute("message", "Employee updated successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("employee", employee);
            return "edit-employee";
        }
    }

    /* -------------------------------------------------------
       DELETE EMPLOYEE
       ------------------------------------------------------- */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {
        try {
            employeeService.deleteEmployee(id);
            model.addAttribute("message", "Employee deleted successfully!");
        } catch (Exception ex) {
            model.addAttribute("error", "Error deleting employee: " + ex.getMessage());
        }
        return "redirect:/web/";
    }
}
