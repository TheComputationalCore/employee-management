package com.empmgmt.controller;

import com.empmgmt.dto.EmployeeDTO;
import com.empmgmt.dto.EmployeeSearchRequest;
import com.empmgmt.dto.PaginatedResponse;
import com.empmgmt.model.EmployeeStatus;
import com.empmgmt.repository.DepartmentRepository;
import com.empmgmt.service.EmployeeService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/employees")
public class EmployeeWebController {

    private final EmployeeService service;
    private final DepartmentRepository departmentRepository;


    /* =============================================================
                         EMPLOYEE LIST
    ============================================================= */
    @GetMapping
    public String employeeList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id,asc") String sort,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String department,
            Model model
    ) {

        EmployeeSearchRequest req = new EmployeeSearchRequest();
        req.setPage(page);
        req.setSize(size);
        req.setSort(sort);
        req.setSearch(search);
        req.setDepartment(department);

        PaginatedResponse<EmployeeDTO> result = service.searchEmployees(req);

        model.addAttribute("employees", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("totalElements", result.getTotalElements());
        model.addAttribute("size", size);

        model.addAttribute("search", search);
        model.addAttribute("department", department);
        model.addAttribute("sort", sort);

        model.addAttribute("departments", departmentRepository.findAll());

        return "employees/list";
    }


    /* =============================================================
                         ADD EMPLOYEE PAGE
    ============================================================= */
    @GetMapping("/add")
    public String addEmployeeForm(Model model) {

        model.addAttribute("employee", new EmployeeDTO());
        model.addAttribute("departments", departmentRepository.findAll());

        return "employees/add";
    }

    @PostMapping("/add")
    public String addEmployee(@ModelAttribute("employee") EmployeeDTO employee) {
        service.createEmployee(employee);
        return "redirect:/web/employees?added";
    }


    /* =============================================================
                        EDIT EMPLOYEE PAGE
    ============================================================= */
    @GetMapping("/edit/{id}")
    public String editEmployeeForm(@PathVariable Long id, Model model) {

        EmployeeDTO employee = service.getEmployee(id);

        model.addAttribute("employee", employee);
        model.addAttribute("departments", departmentRepository.findAll());

        return "employees/edit";
    }

    @PostMapping("/edit/{id}")
    public String updateEmployee(
            @PathVariable Long id,
            @ModelAttribute("employee") EmployeeDTO employee
    ) {

        service.updateEmployee(id, employee);
        return "redirect:/web/employees?updated";
    }


    /* =============================================================
                        INACTIVE EMPLOYEE LIST
    ============================================================= */
    @GetMapping("/inactive")
    public String inactiveEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        EmployeeSearchRequest req = new EmployeeSearchRequest();
        req.setPage(page);
        req.setSize(size);
        req.setStatus(EmployeeStatus.INACTIVE);

        PaginatedResponse<EmployeeDTO> result = service.searchEmployees(req);

        model.addAttribute("employees", result.getContent());
        model.addAttribute("currentPage", result.getPage());
        model.addAttribute("totalPages", result.getTotalPages());
        model.addAttribute("size", size);

        return "employees/inactive";
    }


    /* =============================================================
                        DELETE & RESTORE
    ============================================================= */
    @GetMapping("/delete/{id}")
    public String softDelete(@PathVariable Long id) {
        service.softDeleteEmployee(id);
        return "redirect:/web/employees?deleted";
    }

    @GetMapping("/restore/{id}")
    public String restore(@PathVariable Long id) {
        service.restoreEmployee(id);
        return "redirect:/web/employees/inactive?restored";
    }
}
