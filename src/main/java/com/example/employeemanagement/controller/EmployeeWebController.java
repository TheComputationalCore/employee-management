package com.example.employeemanagement.controller;

import com.example.employeemanagement.model.Employee;
import com.example.employeemanagement.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/web")
public class EmployeeWebController {

    @Autowired
    private EmployeeService employeeService;

    @GetMapping
    public String dashboard(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "8") int size,
            Model model) {

        Page<Employee> employeePage = employeeService.getPaginatedEmployees(page, size);

        model.addAttribute("employees", employeePage.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", employeePage.getTotalPages());
        model.addAttribute("pageSize", size);

        model.addAttribute("totalEmployees", employeeService.countEmployees());
        model.addAttribute("departmentsCount", employeeService.countDepartments());
        model.addAttribute("positionsCount", employeeService.countPositions());

        model.addAttribute("pageTitle", "Dashboard");

        return "index";
    }

    @GetMapping("/employees/new")
    public String showCreateForm(Model model) {
        model.addAttribute("employee", new Employee());
        model.addAttribute("pageTitle", "Add Employee");
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String createEmployee(@ModelAttribute Employee employee) {
        employeeService.createEmployee(employee);
        return "redirect:/web?created=true";
    }

    @GetMapping("/employees/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", employeeService.getEmployeeById(id));
        model.addAttribute("pageTitle", "Edit Employee");
        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String updateEmployee(@PathVariable Long id,
                                 @ModelAttribute Employee employee) {
        employeeService.updateEmployee(id, employee);
        return "redirect:/web?updated=true";
    }

    @GetMapping("/employees/delete/{id}")
    public String deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        return "redirect:/web?deleted=true";
    }
}
