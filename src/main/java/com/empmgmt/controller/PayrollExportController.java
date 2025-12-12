package com.empmgmt.controller;

import com.empmgmt.service.PayrollExportService;

import jakarta.servlet.http.HttpServletResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/export/payroll")
public class PayrollExportController {

    private final PayrollExportService exportService;

    @GetMapping("/excel")
    public void excel(HttpServletResponse response) {
        exportService.exportAllPayrollsExcel(response);
    }

    @GetMapping("/pdf/{id}")
    public void payslipPdf(@PathVariable Long id, HttpServletResponse response) {
        exportService.exportPayrollPdf(id, response);
    }

    @GetMapping("/employee/{empId}")
    public void employeePdf(@PathVariable Long empId, HttpServletResponse response) {
        exportService.exportEmployeePayrollPdf(empId, response);
    }
}
