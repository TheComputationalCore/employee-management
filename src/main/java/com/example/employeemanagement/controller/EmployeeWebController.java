package com.thecomputationalcore.employeemanagement.controller;

import com.thecomputationalcore.employeemanagement.model.Employee;
import com.thecomputationalcore.employeemanagement.service.EmployeeService;

import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/web")
@RequiredArgsConstructor
public class EmployeeWebController {

    private final EmployeeService employeeService;


    /* ==========================================================
       DASHBOARD (LIST EMPLOYEES + SEARCH + SORT + PAGINATION)
       ========================================================== */
    @GetMapping("/")
    public String listEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name") String sort,
            @RequestParam(defaultValue = "asc") String dir,
            @RequestParam(required = false) String search,
            Model model
    ) {

        Page<Employee> employeePage =
                employeeService.getEmployees(page, size, sort, dir, search);

        model.addAttribute("employees", employeePage.getContent());

        /* Pagination metadata */
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("totalItems", employeePage.getTotalElements());

        /* Sorting + search */
        model.addAttribute("sort", sort);
        model.addAttribute("dir", dir);
        model.addAttribute("reverseDir", dir.equals("asc") ? "desc" : "asc");
        model.addAttribute("search", search);

        /* Page title */
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
            @ModelAttribute("employee") Employee employee,
            RedirectAttributes redirectAttributes
    ) {
        employeeService.createEmployee(employee);
        redirectAttributes.addFlashAttribute("message", "Employee added successfully!");
        return "redirect:/web/";
    }



    /* ==========================================================
       EDIT EMPLOYEE
       ========================================================== */
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
            @ModelAttribute("employee") Employee employee,
            RedirectAttributes redirectAttributes
    ) {
        employeeService.updateEmployee(id, employee);
        redirectAttributes.addFlashAttribute("message", "Employee updated successfully!");
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
        employeeService.deleteEmployee(id);
        redirectAttributes.addFlashAttribute("message", "Employee deleted successfully!");
        return "redirect:/web/";
    }
}
