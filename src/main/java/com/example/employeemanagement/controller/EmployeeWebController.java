package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
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
@RequiredArgsConstructor
@RequestMapping("/web")
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* ============================
       DASHBOARD + PAGINATION
    ============================ */
    @GetMapping("/")
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "0") int page) {

        Page<Employee> paged = employeeService.getPaginatedEmployees(page, 8);

        model.addAttribute("employees", paged.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", paged.getTotalPages());

        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        return "index";
    }


    /* ============================
       CREATE EMPLOYEE (FORM)
    ============================ */
    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(@Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) return "create-employee";

        try {
            employeeService.createEmployee(employee);
            redirectAttributes.addFlashAttribute("message", "Employee created successfully!");
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "error.employee", ex.getMessage());
            return "create-employee";
        }

        return "redirect:/web/";
    }


    /* ============================
       EDIT EMPLOYEE (FORM)
    ============================ */
    @GetMapping("/employees/edit/{id}")
    public String showEdit(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) return "edit-employee";

        try {
            employeeService.updateEmployee(id, employee);
            redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
        } catch (IllegalArgumentException ex) {
            result.rejectValue("email", "error.employee", ex.getMessage());
            return "edit-employee";
        }

        return "redirect:/web/";
    }


    /* ============================
       DELETE EMPLOYEE
    ============================ */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {

        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");

        return "redirect:/web/";
    }
}
