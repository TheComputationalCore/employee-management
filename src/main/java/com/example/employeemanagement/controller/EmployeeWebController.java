package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web")
public class EmployeeWebController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeWebController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /* ============================================================
       DASHBOARD + PAGINATED EMPLOYEE LIST
       ============================================================ */
    @GetMapping("/")
    public String listEmployees(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Employee> employeePage = employeeService.getAllEmployeesPaginated(pageable);

        List<Employee> employees = employeePage.getContent();

        model.addAttribute("employees", employees);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);

        // Dashboard metrics
        long totalEmployees = employeePage.getTotalElements();
        long departmentsCount = employees.stream()
                .map(Employee::getDepartment)
                .filter(d -> d != null && !d.isBlank())
                .distinct()
                .count();

        long positionsCount = employees.stream()
                .map(Employee::getPosition)
                .filter(p -> p != null && !p.isBlank())
                .distinct()
                .count();

        model.addAttribute("totalEmployees", totalEmployees);
        model.addAttribute("departmentsCount", departmentsCount);
        model.addAttribute("positionsCount", positionsCount);

        return "index";
    }

    /* ============================================================
       CREATE EMPLOYEE
       ============================================================ */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "create-employee";
        }

        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("message", "Employee created successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            return "create-employee";
        }
    }

    /* ============================================================
       EDIT EMPLOYEE
       ============================================================ */
    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {

        Employee employee = employeeService.getEmployeeById(id);
        model.addAttribute("employee", employee);

        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "edit-employee";
        }

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
            return "redirect:/web/";
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            return "edit-employee";
        }
    }

    /* ============================================================
       DELETE EMPLOYEE
       ============================================================ */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {

        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Could not delete employee: " + e.getMessage());
        }

        return "redirect:/web/";
    }
}
