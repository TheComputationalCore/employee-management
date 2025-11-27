package com.example.employeemanagement.controller;

import com.example.employeemanagement.dto.EmployeeRequestDTO;
import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web")
public class EmployeeWebController {

    private final EmployeeService employeeService;

    /* ===========================================================
       DASHBOARD + PAGINATION + SEARCH
    ============================================================ */

    @GetMapping("/")
    public String dashboard(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "8") int size,
            @RequestParam(defaultValue = "") String search,
            Model model
    ) {

        Page<Employee> employees = employeeService.getPaginatedEmployees(page, size, search);

        model.addAttribute("employees", employees.getContent());
        model.addAttribute("totalPages", employees.getTotalPages());
        model.addAttribute("currentPage", page);
        model.addAttribute("search", search);

        /* Dashboard counters */
        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }

    /* ===========================================================
       CREATE EMPLOYEE
    ============================================================ */

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new EmployeeRequestDTO());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(@ModelAttribute("employee") EmployeeRequestDTO dto,
                                 Model model) {

        try {
            employeeService.createEmployee(dto);
            model.addAttribute("message", "Employee created successfully!");
            return "redirect:/web/";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("pageTitle", "Add Employee");
            return "create-employee";
        }
    }

    /* ===========================================================
       EDIT EMPLOYEE
    ============================================================ */

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Employee emp = employeeService.getEmployeeById(id);

        EmployeeRequestDTO dto = new EmployeeRequestDTO(
                emp.getFirstName(),
                emp.getLastName(),
                emp.getEmail(),
                emp.getPhoneNumber(),
                emp.getDepartment(),
                emp.getPosition(),
                emp.getSalary()
        );

        model.addAttribute("employee", dto);
        model.addAttribute("id", id);
        model.addAttribute("pageTitle", "Edit Employee");

        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @ModelAttribute("employee") EmployeeRequestDTO dto,
                                 Model model) {

        try {
            employeeService.updateEmployee(id, dto);
            return "redirect:/web/?updated=true";
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
            model.addAttribute("id", id);
            model.addAttribute("pageTitle", "Edit Employee");
            return "edit-employee";
        }
    }

    /* ===========================================================
       DELETE EMPLOYEE
    ============================================================ */

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id, Model model) {

        try {
            employeeService.deleteEmployee(id);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }

        return "redirect:/web/";
    }
}
