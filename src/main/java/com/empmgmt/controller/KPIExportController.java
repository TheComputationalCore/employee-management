package com.empmgmt.controller;

import com.empmgmt.service.KPIExportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/web/export/kpi")
public class KPIExportController {

    private final KPIExportService service;

    @GetMapping("/employee/pdf/{employeeId}")
    public void exportEmployeePdf(@PathVariable Long employeeId,
                                  HttpServletResponse response) throws Exception {
        service.exportEmployeeKpiPdf(employeeId, response);
    }

    @GetMapping("/employee/excel/{employeeId}")
    public void exportEmployeeExcel(@PathVariable Long employeeId,
                                    HttpServletResponse response) throws Exception {
        service.exportEmployeeKpiExcel(employeeId, response);
    }

    @GetMapping("/all/excel")
    public void exportAllExcel(HttpServletResponse response) throws Exception {
        service.exportAllKpiExcel(response);
    }
}
