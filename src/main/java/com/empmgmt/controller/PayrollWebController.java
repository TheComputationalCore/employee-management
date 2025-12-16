package com.empmgmt.controller;

import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.PayrollService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/payroll")
public class PayrollWebController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    /* --------------------------------
     * EMPLOYEE: MY PAYROLL
     * -------------------------------- */
    @GetMapping("/my")
    public String myPayroll(Model model) {

        Long employeeId = employeeService.getEmployeeIdFromAuth();

        model.addAttribute("payrolls",
                payrollService.getEmployeePayroll(employeeId));

        return "payroll/my";
    }

    /* --------------------------------
     * ADMIN / HR: ALL PAYROLLS
     * -------------------------------- */
    @GetMapping
    public String payrollList(Model model) {

        model.addAttribute("payrolls",
                payrollService.getAllPayrolls());

        return "payroll/list";
    }

    /* --------------------------------
     * ADMIN / HR: GENERATE PAYROLL
     * -------------------------------- */
    @GetMapping("/generate")
    public String generateForm(Model model) {

        model.addAttribute("employees",
                employeeService.getAllEmployees());

        return "payroll/generate";
    }

    @PostMapping("/generate")
    public String generate(
            @RequestParam Long employeeId,
            @RequestParam String month,
            @RequestParam int year
    ) {

        payrollService.generatePayroll(employeeId, month, year);
        return "redirect:/web/payroll?success";
    }

    /* --------------------------------
     * ADMIN / HR: MARK AS PAID
     * -------------------------------- */
    @PostMapping("/mark-paid/{id}")
    public String markPaid(@PathVariable Long id) {

        payrollService.markAsPaid(id);
        return "redirect:/web/payroll?paid";
    }
}
