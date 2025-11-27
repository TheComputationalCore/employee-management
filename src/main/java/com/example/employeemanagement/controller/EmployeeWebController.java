package com.example.employeemanagement.controller;

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

    private final EmployeeService service;

    @GetMapping("/")
    public String dashboard(Model model,
                            @RequestParam(defaultValue = "0") int page,
                            @RequestParam(defaultValue = "8") int size,
                            @RequestParam(defaultValue = "") String keyword,
                            @RequestParam(defaultValue = "firstName") String sortField,
                            @RequestParam(defaultValue = "asc") String sortDir) {

        Page<Employee> p = service.getEmployeesPaginated(page, size, keyword, sortField, sortDir);

        model.addAttribute("employees", p.getContent());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", p.getTotalPages());
        model.addAttribute("totalEmployees", p.getTotalElements());

        model.addAttribute("keyword", keyword);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("reverseSortDir", sortDir.equals("asc") ? "desc" : "asc");

        model.addAttribute("departmentsCount", p.stream().map(Employee::getDepartment).distinct().count());
        model.addAttribute("positionsCount", p.stream().map(Employee::getPosition).distinct().count());

        return "index";
    }

    @GetMapping("/employees/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        model.addAttribute("employee", service.getEmployeeById(id));
        return "profile";
    }

    @GetMapping("/employees/new")
    public String newForm(Model m) {
        m.addAttribute("employee", new Employee());
        return "create-employee";
    }

    @PostMapping("/employees/new")
    public String create(Employee e) {
        service.createEmployee(e);
        return "redirect:/web/";
    }

    @GetMapping("/employees/edit/{id}")
    public String editForm(@PathVariable Long id, Model m) {
        m.addAttribute("employee", service.getEmployeeById(id));
        return "edit-employee";
    }

    @PostMapping("/employees/edit/{id}")
    public String update(@PathVariable Long id, Employee e) {
        service.updateEmployee(id, e);
        return "redirect:/web/";
    }

    @GetMapping("/employees/delete/{id}")
    public String delete(@PathVariable Long id) {
        service.deleteEmployee(id);
        return "redirect:/web/";
    }
}
