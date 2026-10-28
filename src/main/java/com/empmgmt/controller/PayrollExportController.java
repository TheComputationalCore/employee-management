package com.empmgmt.controller;

import com.empmgmt.model.Payroll;
import com.empmgmt.repository.PayrollRepository;
import com.empmgmt.service.EmployeeService;
import com.empmgmt.service.PayrollExportService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/export/payroll")
public class PayrollExportController {

    private final PayrollExportService exportService;
    private final PayrollRepository payrollRepository;
    private final EmployeeService employeeService;

    @GetMapping("/excel")
    public void excel(HttpServletResponse response) {
        exportService.exportAllPayrollsExcel(response);
    }

    @GetMapping("/pdf/{id}")
    public void payslipPdf(@PathVariable Long id, HttpServletResponse response,
                           Authentication authentication) {
        boolean employee = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_EMPLOYEE".equals(authority.getAuthority()));
        boolean payrollManager = authentication.getAuthorities().stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())
                        || "ROLE_HR".equals(authority.getAuthority()));

        if (employee && !payrollManager) {
            Long employeeId = employeeService.getEmployeeIdFromAuth();
            Payroll payroll = payrollRepository.findById(id)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payslip not found"));
            if (!Objects.equals(payroll.getEmployeeId(), employeeId)) {
                throw new AccessDeniedException("You may only download your own payslips");
            }
        }
        exportService.exportPayrollPdf(id, response);
    }

    @GetMapping("/employee/{empId}")
    public void employeePdf(@PathVariable Long empId, HttpServletResponse response) {
        exportService.exportEmployeePayrollPdf(empId, response);
    }
}
