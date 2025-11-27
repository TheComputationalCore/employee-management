package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.dto.EmployeeWebDTO;
import com.example.employeemanagement.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* ============================================================
       DASHBOARD + PAGINATED TABLE
    ============================================================ */
    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "firstName") String sortField,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String keyword,
            Model model
    ) {

        // Paginated list
        List<EmployeeWebDTO> employees =
                employeeService.getPaginatedEmployees(page, size, sortField, sortDir, keyword);

        long totalRecords = employeeService.getTotalEmployeeCount(keyword);
        int totalPages = (int) Math.ceil((double) totalRecords / size);

        // Dashboard stats
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        // Table + Pagination attributes
        model.addAttribute("employees", employees);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("size", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("keyword", keyword);

        // UI Title
        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }


    /* ============================================================
       CREATE EMPLOYEE (FORM)
    ============================================================ */
    @GetMapping("/employees/new")
    public String newEmployeeForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDTO());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(
            @Valid @ModelAttribute("employee") EmployeeRequestDTO dto,
            BindingResult result,
            RedirectAttributes redirect
    ) {

        if (result.hasErrors()) return "create-employee";

        try {
            employeeService.createEmployee(dto);
            redirect.addFlashAttribute("message", "Employee created successfully!");
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            return "create-employee";
        }

        return "redirect:/web/";
    }


    /* ============================================================
       EDIT EMPLOYEE
    ============================================================ */
    @GetMapping("/employees/edit/{id}")
    public String editEmployeeForm(
            @PathVariable Long id,
            Model model
    ) {

        EmployeeWebDTO emp = employeeService.getEmployeeById(id);

        model.addAttribute("employee", emp);
        model.addAttribute("pageTitle", "Edit Employee");

        return "edit-employee";
    }


    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @Valid @ModelAttribute("employee") EmployeeRequestDTO dto,
            BindingResult result,
            RedirectAttributes redirect
    ) {

        if (result.hasErrors()) return "edit-employee";

        try {
            employeeService.updateEmployee(id, dto);
            redirect.addFlashAttribute("message", "Employee updated successfully!");
        } catch (IllegalArgumentException e) {
            result.rejectValue("email", "error.employee", e.getMessage());
            return "edit-employee";
        }

        return "redirect:/web/";
    }


    /* ============================================================
       DELETE EMPLOYEE
    ============================================================ */
    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(
            @PathVariable Long id,
            RedirectAttributes redirect
    ) {

        try {
            employeeService.deleteEmployee(id);
            redirect.addFlashAttribute("message", "Employee deleted successfully!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/web/";
    }
}
