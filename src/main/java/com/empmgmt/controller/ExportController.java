package com.empmgmt.controller.web;

import com.empmgmt.service.ExportService;

import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/export")
public class ExportController {

    private final ExportService exportService;

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employees/excel")
    public void exportEmployeesExcel(HttpServletResponse response) {
        exportService.exportEmployeesToExcel(response);
    }

    @PreAuthorize("hasAnyRole('ADMIN','HR')")
    @GetMapping("/employees/pdf")
    public void exportEmployeesPdf(HttpServletResponse response) {
        exportService.exportEmployeesToPDF(response);
    }
}
