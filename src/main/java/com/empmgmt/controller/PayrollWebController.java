package com.empmgmt.controller;

import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.PayrollService;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/payroll")
public class PayrollWebController {

    private final PayrollService payrollService;
    private final EmployeeService employeeService;

    @GetMapping("/my")
    public String myPayroll(Model model, Principal principal) {

        Long employeeId = Long.parseLong(principal.getName());

        model.addAttribute("payrolls", payrollService.getEmployeePayroll(employeeId));
        return "payroll/my";
    }


    @GetMapping
    public String payrollList(Model model) {
        model.addAttribute("payrolls", payrollService.getAllPayrolls());
        return "payroll/list";
    }

    @GetMapping("/generate")
    public String generateForm(Model model) {
        model.addAttribute("employees", employeeService.getAllEmployees());
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

    @PostMapping("/mark-paid/{id}")
    public String markPaid(@PathVariable Long id) {
        payrollService.markAsPaid(id);
        return "redirect:/web/payroll?paid";
    }

}
