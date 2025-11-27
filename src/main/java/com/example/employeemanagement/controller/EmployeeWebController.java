package com.thecomputationalcore.employeemanagement.controller;

import com.thecomputationalcore.employeemanagement.dto.EmployeeDto;
import com.thecomputationalcore.employeemanagement.exception.ResourceNotFoundException;
import com.thecomputationalcore.employeemanagement.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web/employees")
@RequiredArgsConstructor
@Slf4j
public class EmployeeWebController {

    private final EmployeeService employeeService;

    // Display list of employees
    @GetMapping
    public String listEmployees(Model model) {
        log.info("WEB: Listing all employees");
        model.addAttribute("employees", employeeService.getAllEmployees());
        return "index";
    }

    // Show creation form
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.info("WEB: Loading employee creation form");
        model.addAttribute("employee", new EmployeeDto());
        return "create-employee";
    }

    // Handle creation
    @PostMapping("/new")
    public String createEmployee(@Valid @ModelAttribute("employee") EmployeeDto employeeDto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.warn("WEB: Validation failed while creating employee");
            return "create-employee";
        }

        try {
            employeeService.createEmployee(employeeDto);
            redirectAttributes.addFlashAttribute("success", "Employee created successfully!");
            return "redirect:/web/employees";
        } catch (IllegalArgumentException e) {
            log.error("WEB: Email conflict creating employee - {}", e.getMessage());
            result.rejectValue("email", "error.employee", e.getMessage());
            return "create-employee";
        }
    }

    // Show edit form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            log.info("WEB: Loading edit form for employee ID {}", id);
            EmployeeDto employee = employeeService.getEmployeeById(id);
            model.addAttribute("employee", employee);
            return "edit-employee";
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/web/employees";
        }
    }

    // Handle update
    @PostMapping("/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") EmployeeDto employeeDto,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            log.warn("WEB: Validation failed updating employee ID {}", id);
            return "edit-employee";
        }

        try {
            employeeService.updateEmployee(id, employeeDto);
            redirectAttributes.addFlashAttribute("success", "Employee updated successfully!");
            return "redirect:/web/employees";
        } catch (IllegalArgumentException e) {
            log.error("WEB: Email conflict updating employee - {}", e.getMessage());
            result.rejectValue("email", "error.employee", e.getMessage());
            return "edit-employee";
        }
    }

    // Handle deletion
    @GetMapping("/delete/{id}")
    public String deleteEmployee(@PathVariable Long id,
                                 RedirectAttributes redirectAttributes) {
        log.info("WEB: Deleting employee ID {}", id);

        try {
            employeeService.deleteEmployee(id);
            redirectAttributes.addFlashAttribute("success", "Employee deleted successfully!");
        } catch (ResourceNotFoundException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/employees";
    }
}
